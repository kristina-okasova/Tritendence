package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.fragments.EditAttendanceSheetFragment;
import com.example.tritendence.fragments.FilledAttendanceSheetFragment;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditAttendanceSheetActivity extends AppCompatActivity {
    private EditAttendanceSheetFragment editAttendanceSheetFragment;
    private String signedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance_sheet);

        this.signedUser = getIntent().getExtras().getString("SIGNED_USER");
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.attendanceFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.editAttendanceSheetFragment = new EditAttendanceSheetFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, this.editAttendanceSheetFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra("SIGNED_USER", this.signedUser);
                homePage.putExtra("TRIATHLON_CLUB", club);
                homePage.putExtra("SELECTED_FRAGMENT", item.getItemId());
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

        Toast.makeText(this, "Už bol pridaný maximálny počet trénerov.", Toast.LENGTH_LONG).show();
    }
}