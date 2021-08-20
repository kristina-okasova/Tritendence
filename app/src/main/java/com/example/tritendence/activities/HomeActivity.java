package com.example.tritendence.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthletesFragment;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private AttendanceFragment attendanceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        System.out.println("on create homeActivity");
        this.attendanceFragment = new AttendanceFragment(this);
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, this.attendanceFragment).commit();}

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                Fragment selectedFragment;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attendance_selection_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.attendanceFragment.clearTimetable();

        switch (item.getItemId()) {
            case R.id.swimmingSelection:
                this.attendanceFragment.addGroups(getString(R.string.SWIMMING_DB));
                break;
            case R.id.athleticsSelection:
                this.attendanceFragment.addGroups(getString(R.string.ATHLETICS_DB));
                break;
            case R.id.cyclingSelection:
                this.attendanceFragment.addGroups(getString(R.string.CYCLING_DB));
                break;
            case R.id.allTrainings:
                this.attendanceFragment.addGroups(getString(R.string.EMPTY_STRING));
                break;
            default:
                break;
        }

        this.attendanceFragment.updateExpandableTimetable();
        return super.onOptionsItemSelected(item);
    }
}