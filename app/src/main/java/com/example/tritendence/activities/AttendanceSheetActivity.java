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
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendanceSheetActivity extends AppCompatActivity {
    //Intent's extras
    private TriathlonClub club;
    private String signedUser, sportSelection;
    private LoadData loadData;

    private AttendanceSheetFragment attendanceSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);

        //Getting extras of the intent.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        this.findTypeOfSignedUser(navigation);
        this.moveLayoutWithKeyboard(navigation);

        //Setting currently selected navigation item and navigation listener.
        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Attaching fragment to the activity.
        this.attendanceSheetFragment = new AttendanceSheetFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, this.attendanceSheetFragment).commit();
    }

    private void findTypeOfSignedUser(BottomNavigationView navigation) {
        if (this.club.getAdminOfClub().getFullName().equals(signedUser)) {
            navigation.getMenu().clear();
            navigation.inflateMenu(R.menu.home_bottom_menu_admin);
        }
    }

    private void moveLayoutWithKeyboard(BottomNavigationView navigation) {
        FragmentContainerView fragment = findViewById(R.id.homeFragment);
        final View activityRootView = findViewById(R.id.attendanceSheetActivity);

        //Changing margin of the whole layout when keyboard is shown. The change is caused by hidden navigation.
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight() - 2*navigation.getHeight();

            //When the keyboard is shown, the margin is set to zero.
            if (heightDiff > navigation.getHeight()) {
                navigation.setVisibility(View.GONE);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                fragment.setLayoutParams(params);
                fragment.requestLayout();
            }

            //When the keyboard is hidden, the margin is restored.
            if (heightDiff == 0) {
                navigation.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 60);
                fragment.setLayoutParams(params);
                fragment.requestLayout();
            }
        });
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

    public void addTrainerSelection(View view) {
        //Add new trainer's spinner for selection of the second trainer.
        ConstraintLayout secondTrainer = findViewById(R.id.secondTrainerSelection);
        if (secondTrainer.getVisibility() == View.GONE) {
            secondTrainer.setVisibility(View.VISIBLE);
            this.attendanceSheetFragment.addTrainersNames(findViewById(R.id.secondTrainersName));
            findViewById(R.id.attendanceSheetLayout).invalidate();
            return;
        }

        //Add new trainer's spinner for selection of the second trainer.
        ConstraintLayout thirdTrainer = findViewById(R.id.thirdTrainerSelection);
        if (thirdTrainer.getVisibility() == View.GONE) {
            thirdTrainer.setVisibility(View.VISIBLE);
            this.attendanceSheetFragment.addTrainersNames(findViewById(R.id.thirdTrainersName));
            findViewById(R.id.attendanceSheetLayout).invalidate();
        }
    }
}