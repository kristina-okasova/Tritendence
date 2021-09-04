package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AttendanceSheetFragment;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendanceSheetActivity extends AppCompatActivity {
    private AttendanceSheetFragment attendanceSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        FragmentContainerView fragment = findViewById(R.id.homeFragment);
        final View activityRootView = findViewById(R.id.attendanceSheetActivity);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight() - 2*navigation.getHeight();

            if (heightDiff > navigation.getHeight()) {
                navigation.setVisibility(View.GONE);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                fragment.setLayoutParams(params);
                fragment.requestLayout();
            }

            if (heightDiff == 0) {
                navigation.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 60);
                fragment.setLayoutParams(params);
                fragment.requestLayout();
            }

        });

        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.attendanceSheetFragment = new AttendanceSheetFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, this.attendanceSheetFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
                String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), club);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void showGroupInformation(View view) {
        Intent groupInformationPage = new Intent(this, GroupInformationActivity.class);
        startActivity(groupInformationPage);
        finish();
    }

    public void addTrainerSelection(View view) {
        ConstraintLayout secondTrainer = findViewById(R.id.secondTrainerSelection);
        if (secondTrainer.getVisibility() == View.GONE) {
            secondTrainer.setVisibility(View.VISIBLE);
            this.attendanceSheetFragment.addTrainersNames(findViewById(R.id.secondTrainersName));
            findViewById(R.id.attendanceSheetLayout).invalidate();
            return;
        }

        ConstraintLayout thirdTrainer = findViewById(R.id.thirdTrainerSelection);
        if (thirdTrainer.getVisibility() == View.GONE) {
            thirdTrainer.setVisibility(View.VISIBLE);
            this.attendanceSheetFragment.addTrainersNames(findViewById(R.id.thirdTrainersName));
            findViewById(R.id.attendanceSheetLayout).invalidate();
        }
    }
}