package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.fragments.EditAttendanceSheetFragment;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditAttendanceSheetActivity extends AppCompatActivity {
    private EditAttendanceSheetFragment editAttendanceSheetFragment;
    private TriathlonClub club;
    private String signedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance_sheet);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.editAttendanceSheetFragment = new EditAttendanceSheetFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, this.editAttendanceSheetFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                LoadData loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
                homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), loadData);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void addTrainerSelection(View view) {
        ConstraintLayout secondTrainer = findViewById(R.id.secondTrainerSelectionEdit);
        if (secondTrainer.getVisibility() == View.GONE) {
            secondTrainer.setVisibility(View.VISIBLE);
            this.editAttendanceSheetFragment.addTrainersNames(findViewById(R.id.secondTrainersNameEdit), this.signedUser);
            findViewById(R.id.editAttendanceSheetLayout).invalidate();
            return;
        }

        ConstraintLayout thirdTrainer = findViewById(R.id.thirdTrainerSelectionEdit);
        if (thirdTrainer.getVisibility() == View.GONE) {
            thirdTrainer.setVisibility(View.VISIBLE);
            this.editAttendanceSheetFragment.addTrainersNames(findViewById(R.id.thirdTrainersNameEdit), this.signedUser);
            findViewById(R.id.editAttendanceSheetLayout).invalidate();
            return;
        }

        Toast.makeText(this, getString(R.string.MAX_AMOUNT_OF_TRAINERS), Toast.LENGTH_LONG).show();
    }

    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
    }
}