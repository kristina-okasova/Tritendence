package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfilFragment;
import com.example.tritendence.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.database.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.navigation = findViewById(R.id.bottomNavigationView);
        this.navigation.setOnNavigationItemSelectedListener(navigationListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new AttendanceFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.attendanceFragment:
                            selectedFragment = new AttendanceFragment();
                            break;
                        case R.id.groupsFragment:
                            selectedFragment = new GroupsFragment();
                            break;
                        case R.id.profilFragment:
                            selectedFragment = new ProfilFragment();
                            break;
                        case R.id.settingsFragment:
                            selectedFragment = new SettingsFragment();
                            break;
                        default:
                            return false;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();
                    return true;
                }
            };
}