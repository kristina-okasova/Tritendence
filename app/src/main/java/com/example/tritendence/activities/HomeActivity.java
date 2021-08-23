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
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

public class HomeActivity extends AppCompatActivity implements Serializable {
    private AttendanceFragment attendanceFragment;
    private Fragment selectedFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.attendanceFragment = new AttendanceFragment(this);
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
            default:
                selectedFragment = this.attendanceFragment;
        }
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(selectedItemID);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();
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
                    default:
                        return false;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();
                return true;
            };


    public void showGroupInformation(View view) {
        TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        TextView nameOfGroup = view.findViewById(R.id.nameOfGroupInList);
        Intent groupInformationPage = new Intent(this, GroupInformationActivity.class);
        groupInformationPage.putExtra("GROUP_NAME", nameOfGroup.getText().toString());
        groupInformationPage.putExtra("TRIATHLON_CLUB", club);
        groupInformationPage.putExtra("SIGNED_USER", signedUser);
        startActivity(groupInformationPage);
        finish();
    }

    public void showAthleteInformation(View view) {
        TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        TextView nameOfAthlete = view.findViewById(R.id.nameOfAthleteInList);
        Intent athleteInformationPage = new Intent(this, AthleteInformationActivity.class);
        athleteInformationPage.putExtra("ATHLETE_NAME", nameOfAthlete.getText().toString());
        athleteInformationPage.putExtra("TRIATHLON_CLUB", club);
        athleteInformationPage.putExtra("SIGNED_USER", signedUser);
        startActivity(athleteInformationPage);
        finish();
    }
}