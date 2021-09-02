package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.FilledAttendanceSheetFragment;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FilledAttendanceSheetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filled_attendance_sheet);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        FilledAttendanceSheetFragment filledAttendanceSheetFragment = new FilledAttendanceSheetFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, filledAttendanceSheetFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
                String signedUser = getIntent().getExtras().getString("SIGNED_USER");
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra("SIGNED_USER", signedUser);
                homePage.putExtra("TRIATHLON_CLUB", club);
                homePage.putExtra("SELECTED_FRAGMENT", item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void editAttendanceSheet(View view) {
        TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        AttendanceData attendanceData = (AttendanceData) getIntent().getExtras().getSerializable("ATTENDANCE_DATA");
        Intent editAttendancePage = new Intent(this, EditAttendanceSheetActivity.class);
        editAttendancePage.putExtra("SIGNED_USER", signedUser);
        editAttendancePage.putExtra("TRIATHLON_CLUB", club);
        editAttendancePage.putExtra("ATTENDANCE_DATA", attendanceData);
        startActivity(editAttendancePage);
        finish();
    }
}