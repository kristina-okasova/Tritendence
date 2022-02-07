package com.example.tritendence.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthletesFragment;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
import com.example.tritendence.fragments.TrainersFragment;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Admin;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends AppCompatActivity {
    //Intent's extras
    private Fragment selectedFragment;
    private TriathlonClub club;
    private LoadData loadData;
    private String signedUser, sportSelection, theme;

    //Fragments
    private AttendanceFragment attendanceFragment;
    private GroupsFragment groupsFragment;
    private ProfileFragment profileFragment;
    private AthletesFragment athletesFragment;
    private TrainersFragment trainersFragment;

    private BottomNavigationView navigation;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting user's theme.
        this.theme = getIntent().getExtras().getString(getString(R.string.THEME_EXTRA));
        switch(this.theme) {
            case "DarkRed":
                setTheme(R.style.Theme_DarkRed);
                break;
            case "DarkBlue":
                setTheme(R.style.Theme_DarkBlue);
                break;
            case "LightRed":
                setTheme(R.style.Theme_LightRed);
                break;
            case "LightBlue":
                setTheme(R.style.Theme_LightBlue);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Getting extras of the intent and setting load data's activity.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.loadData.setActivity(this);

        //Creating instances of all fragments available from Home Activity.
        this.attendanceFragment = new AttendanceFragment(this);
        this.groupsFragment = new GroupsFragment();
        this.profileFragment = new ProfileFragment(this);
        this.athletesFragment = new AthletesFragment();
        this.trainersFragment = new TrainersFragment();

        this.navigation = findViewById(R.id.bottomNavigationView);
        this.findTypeOfSignedUser();

        //Finding fragment based on selected item ID passed from the previous activity.
        int selectedItemID = getIntent().getExtras().getInt(getString(R.string.SELECTED_FRAGMENT_EXTRA));
        switch (selectedItemID) {
            case R.id.groupsFragment:
                this.selectedFragment = this.groupsFragment;
                break;
            case R.id.profileFragment:
                this.selectedFragment = this.profileFragment;
                break;
            case R.id.athletesFragment:
                this.selectedFragment = this.athletesFragment;
                break;
            case R.id.trainersFragment:
                this.selectedFragment = this.trainersFragment;
                break;
            default:
                this.selectedFragment = this.attendanceFragment;
        }
        //Setting currently selected navigation item and navigation listener.
        this.navigation.setSelectedItemId(selectedItemID);
        this.navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Attaching fragment to the activity.
        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, this.selectedFragment).commit();
    }

    private void findTypeOfSignedUser() {
        if (this.club.getAdminOfClub().getFullName().equals(signedUser)) {
            this.navigation.getMenu().clear();
            this.navigation.inflateMenu(R.menu.home_bottom_menu_admin);
        }
        else {
            this.navigation.getMenu().clear();
            this.navigation.inflateMenu(R.menu.home_bottom_menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                this.loadData.setActivity(this);
                //Finding fragment based on the selected item of the menu.
                switch (item.getItemId()) {
                    case R.id.attendanceFragment:
                        selectedFragment = this.attendanceFragment;
                        break;
                    case R.id.groupsFragment:
                        selectedFragment = this.groupsFragment;
                        break;
                    case R.id.profileFragment:
                        selectedFragment = this.profileFragment;
                        break;
                    case R.id.athletesFragment:
                        selectedFragment = this.athletesFragment;
                        break;
                    case R.id.trainersFragment:
                        selectedFragment = this.trainersFragment;
                        break;
                    default:
                        return false;
                }

                //Attaching fragment to the activity.
                getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, selectedFragment).commit();
                return true;
            };

    public void showGroupInformation(View view) {
        TextView nameOfGroup = view.findViewById(R.id.nameOfGroupInList);

        //Creating intent of Group Information Activity to show information about the selected group.
        Intent groupInformationPage = new Intent(this, GroupInformationActivity.class);
        groupInformationPage.putExtra(getString(R.string.GROUP_NAME_EXTRA), nameOfGroup.getText().toString());
        groupInformationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        groupInformationPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        groupInformationPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        groupInformationPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        groupInformationPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        startActivity(groupInformationPage);
        finish();
    }

    public void showAthleteInformation(View view) {
        TextView nameOfAthlete = view.findViewById(R.id.nameOfAthleteInList);

        //Creating intent of Athlete Information Activity to show information about the selected athlete.
        Intent athleteInformationPage = new Intent(this, AthleteInformationActivity.class);
        athleteInformationPage.putExtra(getString(R.string.ATHLETE_NAME_EXTRA), nameOfAthlete.getText().toString());
        athleteInformationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        athleteInformationPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        athleteInformationPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        athleteInformationPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        athleteInformationPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        startActivity(athleteInformationPage);
        finish();
    }

    public void showTrainerInformation(View view) {
        TextView nameOfTrainer = view.findViewById(R.id.nameOfTrainerInList);

        //Creating intent of Trainer Information Activity to show information about the selected trainer.
        Intent trainerInformationPage = new Intent(this, TrainerInformationActivity.class);
        trainerInformationPage.putExtra(getString(R.string.TRAINER_NAME_EXTRA), nameOfTrainer.getText().toString());
        trainerInformationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        trainerInformationPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        trainerInformationPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        trainerInformationPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        trainerInformationPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        startActivity(trainerInformationPage);
        finish();
    }

    public void addGroup(View view) {
        //Creating intent of Add Group Activity to show layout to add new group.
        Intent addGroupPage = new Intent(this, AddGroupActivity.class);
        addGroupPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addGroupPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        addGroupPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        addGroupPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        addGroupPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        startActivity(addGroupPage);
        finish();
    }

    public void addAthlete(View view) {
        //Creating intent of Add Group Activity to show layout to add new athlete.
        Intent addAthletePage = new Intent(this, AddAthleteActivity.class);
        addAthletePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addAthletePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        addAthletePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        addAthletePage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        addAthletePage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        startActivity(addAthletePage);
        finish();
    }

    public void addTrainer(View view) {
        //Creating intent of Add Group Activity to show layout to add new athlete.
        Intent addTrainerPage = new Intent(this, AddTrainerActivity.class);
        addTrainerPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addTrainerPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        addTrainerPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        addTrainerPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        addTrainerPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        startActivity(addTrainerPage);
        finish();
    }

    public void updateSportSelection(String sportSelection) {
        this.sportSelection = sportSelection;
    }

    public void changeTheme(String theme) {
        Member member = null;
        for (Member trainer : this.club.getTrainersSortedByAlphabet()) {
            if (trainer instanceof Trainer && trainer.getFullName().equals(this.signedUser))
                member = trainer;
        }
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser))
            member = this.club.getAdminOfClub();

        Intent homePage = new Intent(this, HomeActivity.class);
        homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        homePage.putExtra(getString(R.string.THEME_EXTRA), theme);
        if (member instanceof Trainer)
            homePage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), ((Trainer) member).getSport());
        else
            homePage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), ((Admin) Objects.requireNonNull(member)).getSport());

        startActivity(homePage);
        finish();
    }

    public String getSportSelection() {
        return this.sportSelection;
    }

    public String getThemesName() {
        return this.theme;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;

        //Notify all fragment that has been attached to the activity before.
        if (this.attendanceFragment.isAdded())
            this.attendanceFragment.notifyAboutChange(this.club);

        if (this.groupsFragment.isAdded())
            this.groupsFragment.notifyAboutChange(this.club);

        if (this.profileFragment.isAdded())
            this.profileFragment.notifyAboutChange(this.club);

        if (this.athletesFragment.isAdded())
            this.athletesFragment.notifyAboutChange(this.club);

        if (this.trainersFragment.isAdded())
            this.trainersFragment.notifyAboutChange(this.club);
    }
}