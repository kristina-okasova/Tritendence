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
    private TriathlonClub club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filled_attendance_sheet);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        FilledAttendanceSheetFragment filledAttendanceSheetFragment = new FilledAttendanceSheetFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, filledAttendanceSheetFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void editAttendanceSheet(View view) {
        TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        LoadData loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        AttendanceData attendanceData = (AttendanceData) getIntent().getExtras().getSerializable(getString(R.string.ATTENDANCE_DATA_EXTRA));
        Intent editAttendancePage = new Intent(this, EditAttendanceSheetActivity.class);
        editAttendancePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        editAttendancePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), club);
        editAttendancePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), loadData);
        editAttendancePage.putExtra(getString(R.string.ATTENDANCE_DATA_EXTRA), attendanceData);
        startActivity(editAttendancePage);
        finish();
    }

    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
    }
}