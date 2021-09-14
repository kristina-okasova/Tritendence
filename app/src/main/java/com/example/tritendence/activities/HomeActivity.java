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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.SimpleTimeZone;

public class HomeActivity extends AppCompatActivity {
    //Intent's extras
    private Fragment selectedFragment;
    private TriathlonClub club;
    private LoadData loadData;
    private String signedUser;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Getting extras of the intent and setting load data's activity.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.loadData.setActivity(this);

        //Creating instances of all fragments available from Home Activity.
        this.attendanceFragment = new AttendanceFragment(this);
        this.groupsFragment = new GroupsFragment();
        this.profileFragment = new ProfileFragment();
        this.athletesFragment = new AthletesFragment();
        this.trainersFragment = new TrainersFragment();

        this.navigation = findViewById(R.id.bottomNavigationView);
        this.findTypeOfSignedUser();

        //Finding fragment based on selected item ID passed from the previous activity.
        int selectedItemID = getIntent().getExtras().getInt(getString(R.string.SELECTED_FRAGMENT_EXTRA));
        switch (selectedItemID) {
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
                selectedFragment = this.attendanceFragment;
        }
        //Setting currently selected navigation item and navigation listener.
        this.navigation.setSelectedItemId(selectedItemID);
        this.navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Attaching fragment to the activity.
        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, selectedFragment).commit();
    }

    private void findTypeOfSignedUser() {
        if (this.club.getAdminOfClub().getFullName().equals(signedUser)) {
            this.navigation.getMenu().clear();
            this.navigation.inflateMenu(R.menu.home_bottom_menu_admin);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
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
        startActivity(trainerInformationPage);
        finish();
    }

    public void addGroup(View view) {
        //Creating intent of Add Group Activity to show layout to add new group.
        Intent addGroupPage = new Intent(this, AddGroupActivity.class);
        addGroupPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addGroupPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        addGroupPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        startActivity(addGroupPage);
        finish();
    }

    public void addAthlete(View view) {
        //Creating intent of Add Group Activity to show layout to add new athlete.
        Intent addAthletePage = new Intent(this, AddAthleteActivity.class);
        addAthletePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addAthletePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        addAthletePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        startActivity(addAthletePage);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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