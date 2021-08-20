package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthletesFragment;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.AttendanceSheetFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendanceSheetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);
        System.out.println("on create attendanceSheetActivity");
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new AttendanceSheetFragment()).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                Fragment selectedFragment;

                switch (item.getItemId()) {
                    case R.id.attendanceFragment:
                        selectedFragment = new AttendanceFragment(new HomeActivity());
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
        Intent groupInformationPage = new Intent(this, GroupInformationActivity.class);
        startActivity(groupInformationPage);
        finish();
    }
}