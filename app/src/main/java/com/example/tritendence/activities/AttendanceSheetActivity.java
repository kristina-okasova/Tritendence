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
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendanceSheetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new AttendanceSheetFragment()).commit();
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

    public void showGroupInformation(View view) {
        Intent groupInformationPage = new Intent(this, GroupInformationActivity.class);
        startActivity(groupInformationPage);
        finish();
    }
}