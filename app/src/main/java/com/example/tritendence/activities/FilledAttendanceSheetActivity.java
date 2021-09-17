package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.FilledAttendanceSheetFragment;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FilledAttendanceSheetActivity extends AppCompatActivity {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String signedUser, sportSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filled_attendance_sheet);

        //Getting extras of the intent.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        this.findTypeOfSignedUser(navigation);

        //Setting currently selected navigation item and navigation listener.
        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Attaching fragment to the activity.
        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, new FilledAttendanceSheetFragment()).commit();
    }

    private void findTypeOfSignedUser(BottomNavigationView navigation) {
        if (this.club.getAdminOfClub().getFullName().equals(signedUser)) {
            navigation.getMenu().clear();
            navigation.inflateMenu(R.menu.home_bottom_menu_admin);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                //Creating new intent of Home Activity as part of navigation listener.
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
                homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
                homePage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void editAttendanceSheet(View view) {
        AttendanceData attendanceData = (AttendanceData) getIntent().getExtras().getSerializable(getString(R.string.ATTENDANCE_DATA_EXTRA));

        //Creating new intent of Attendance Sheet Activity to edit attendance data.
        Intent attendancePage = new Intent(this, AttendanceSheetActivity.class);
        attendancePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        attendancePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        attendancePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        attendancePage.putExtra(getString(R.string.ATTENDANCE_DATA_EXTRA), attendanceData);
        startActivity(attendancePage);
        finish();
    }
}