package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthletesFragment;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
import com.example.tritendence.fragments.TrainersFragment;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.zip.Inflater;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private AttendanceFragment attendanceFragment;
    private Fragment selectedFragment;
    private TriathlonClub club;
    private String signedUser;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.signedUser = getIntent().getExtras().getString("SIGNED_USER");
        this.attendanceFragment = new AttendanceFragment(this);

        this.navigation = findViewById(R.id.bottomNavigationView);
        this.findTypeOfSignedUser();
        int selectedItemID = getIntent().getExtras().getInt("SELECTED_FRAGMENT");
        switch (selectedItemID) {
            case R.id.attendanceFragment:
                selectedFragment = this.attendanceFragment;
                break;
            case R.id.groupsFragment:
                selectedFragment = new GroupsFragment();
                break;
            case R.id.profileFragment:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.athletesFragment:
                selectedFragment = new AthletesFragment();
                break;
            case R.id.trainersFragment:
                selectedFragment = new TrainersFragment();
                break;
            default:
                selectedFragment = this.attendanceFragment;
        }
        this.navigation.setSelectedItemId(selectedItemID);
        this.navigation.setOnNavigationItemSelectedListener(navigationListener);

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
                switch (item.getItemId()) {
                    case R.id.attendanceFragment:
                        selectedFragment = this.attendanceFragment;
                        break;
                    case R.id.groupsFragment:
                        selectedFragment = new GroupsFragment();
                        break;
                    case R.id.profileFragment:
                        selectedFragment = new ProfileFragment();
                        break;
                    case R.id.athletesFragment:
                        selectedFragment = new AthletesFragment();
                        break;
                    case R.id.trainersFragment:
                        selectedFragment = new TrainersFragment();
                        break;
                    default:
                        return false;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, selectedFragment).commit();
                return true;
            };


    public void showGroupInformation(View view) {
        TextView nameOfGroup = view.findViewById(R.id.nameOfGroupInList);
        Intent groupInformationPage = new Intent(this, GroupInformationActivity.class);
        groupInformationPage.putExtra("GROUP_NAME", nameOfGroup.getText().toString());
        groupInformationPage.putExtra("TRIATHLON_CLUB", this.club);
        groupInformationPage.putExtra("SIGNED_USER", this.signedUser);
        startActivity(groupInformationPage);
        finish();
    }

    public void showAthleteInformation(View view) {
        TextView nameOfAthlete = view.findViewById(R.id.nameOfAthleteInList);
        Intent athleteInformationPage = new Intent(this, AthleteInformationActivity.class);
        athleteInformationPage.putExtra("ATHLETE_NAME", nameOfAthlete.getText().toString());
        athleteInformationPage.putExtra("TRIATHLON_CLUB", this.club);
        athleteInformationPage.putExtra("SIGNED_USER", this.signedUser);
        startActivity(athleteInformationPage);
        finish();
    }

    public void showTrainerInformation(View view) {
        TextView nameOfTrainer = view.findViewById(R.id.nameOfTrainerInList);
        Intent trainerInformationPage = new Intent(this, TrainerInformationActivity.class);
        trainerInformationPage.putExtra("TRAINER_NAME", nameOfTrainer.getText().toString());
        trainerInformationPage.putExtra("TRIATHLON_CLUB", this.club);
        trainerInformationPage.putExtra("SIGNED_USER", this.signedUser);
        startActivity(trainerInformationPage);
        finish();
    }

    public void addGroup(View view) {
        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        Intent addGroupPage = new Intent(this, AddGroupActivity.class);
        addGroupPage.putExtra("TRIATHLON_CLUB", this.club);
        addGroupPage.putExtra("SIGNED_USER", signedUser);
        startActivity(addGroupPage);
        finish();
    }

    public void addAthlete(View view) {
        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        Intent addAthletePage = new Intent(this, AddAthleteActivity.class);
        addAthletePage.putExtra("TRIATHLON_CLUB", this.club);
        addAthletePage.putExtra("SIGNED_USER", signedUser);
        startActivity(addAthletePage);
        finish();
    }
}