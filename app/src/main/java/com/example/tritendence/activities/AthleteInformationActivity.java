package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthleteInformationFragment;
import com.example.tritendence.fragments.AthletesFragment;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.GroupInformationFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.tritendence.R.*;

public class AthleteInformationActivity extends AppCompatActivity {
    private AthleteInformationFragment athleteInformationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_information);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.athleteInformationFragment = new AthleteInformationFragment(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.athleteFragmentContainerView, this.athleteInformationFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                Fragment selectedFragment;

                switch (item.getItemId()) {
                    case R.id.attendanceFragment:
                        selectedFragment = new AttendanceFragment();
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

                getSupportFragmentManager().beginTransaction().replace(R.id.athleteFragmentContainerView, selectedFragment).commit();
                return true;
            };
}