package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.attendanceFragment = new AttendanceFragment(this);

        this.navigation = findViewById(R.id.bottomNavigationView);
        this.findTypeOfSignedUser();
        int selectedItemID = getIntent().getExtras().getInt(getString(R.string.SELECTED_FRAGMENT_EXTRA));
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
        groupInformationPage.putExtra(getString(R.string.GROUP_NAME_EXTRA), nameOfGroup.getText().toString());
        groupInformationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        groupInformationPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        startActivity(groupInformationPage);
        finish();
    }

    public void showAthleteInformation(View view) {
        TextView nameOfAthlete = view.findViewById(R.id.nameOfAthleteInList);
        Intent athleteInformationPage = new Intent(this, AthleteInformationActivity.class);
        athleteInformationPage.putExtra(getString(R.string.ATHLETE_NAME_EXTRA), nameOfAthlete.getText().toString());
        athleteInformationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        athleteInformationPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        startActivity(athleteInformationPage);
        finish();
    }

    public void showTrainerInformation(View view) {
        TextView nameOfTrainer = view.findViewById(R.id.nameOfTrainerInList);
        Intent trainerInformationPage = new Intent(this, TrainerInformationActivity.class);
        trainerInformationPage.putExtra(getString(R.string.TRAINER_NAME_EXTRA), nameOfTrainer.getText().toString());
        trainerInformationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        trainerInformationPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        startActivity(trainerInformationPage);
        finish();
    }

    public void addGroup(View view) {
        String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        Intent addGroupPage = new Intent(this, AddGroupActivity.class);
        addGroupPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addGroupPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        startActivity(addGroupPage);
        finish();
    }

    public void addAthlete(View view) {
        String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        Intent addAthletePage = new Intent(this, AddAthleteActivity.class);
        addAthletePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addAthletePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        startActivity(addAthletePage);
        finish();
    }
}