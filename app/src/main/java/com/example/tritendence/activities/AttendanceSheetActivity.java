package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.AttendanceSheetFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
import com.example.tritendence.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendanceSheetActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private TextView nameOfGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);

        this.navigation = findViewById(R.id.bottomNavigationView);
        this.navigation.setOnNavigationItemSelectedListener(navigationListener);

        this.nameOfGroup = findViewById(R.id.nameOfGroup);
        String groupName = getIntent().getExtras().getString("GROUP_NAME");
        String time = getIntent().getExtras().getString("TRAINING_TIME");
        String sport = getIntent().getExtras().getString("SPORT_TYPE");
        this.nameOfGroup.setText(String.format("%s\n%s - %s", groupName, sport, time));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new AttendanceSheetFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                Fragment selectedFragment = null;

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
                    case R.id.settingsFragment:
                        selectedFragment = new SettingsFragment();
                        break;
                    default:
                        return false;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();
                return true;
            };
}