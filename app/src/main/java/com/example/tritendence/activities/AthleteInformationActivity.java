package com.example.tritendence.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthleteInformationFragment;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Athlete;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.tritendence.R.*;

public class AthleteInformationActivity extends AppCompatActivity {
    private TriathlonClub club;
    private AthleteInformationFragment athleteInformationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_information);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(string.TRIATHLON_CLUB_EXTRA));
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(id.athletesFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.athleteInformationFragment = new AthleteInformationFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.athleteFragmentContainerView, this.athleteInformationFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                String signedUser = getIntent().getExtras().getString(getString(string.SIGNED_USER_EXTRA));
                LoadData loadData = (LoadData) getIntent().getExtras().getSerializable(getString(string.LOAD_DATA_EXTRA));
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(string.TRIATHLON_CLUB_EXTRA), this.club);
                homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), loadData);
                homePage.putExtra(getString(string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteAthlete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(string.DELETE_ATHLETE));
        builder.setMessage(getString(string.DELETE_ATHLETE_QUESTION));

        builder.setPositiveButton(getString(string.YES),
                (dialog, which) -> this.athleteInformationFragment.deleteAthlete());
        builder.setNegativeButton(getString(string.NO), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void editAthlete(View view) {
        Athlete editAthlete = this.athleteInformationFragment.findSelectedAthlete();
        String signedUser = getIntent().getExtras().getString(getString(string.SIGNED_USER_EXTRA));
        Intent addAthletePage = new Intent(this, AddAthleteActivity.class);
        addAthletePage.putExtra(getString(string.TRIATHLON_CLUB_EXTRA), this.club);
        addAthletePage.putExtra(getString(string.SIGNED_USER_EXTRA), signedUser);
        addAthletePage.putExtra(getString(string.EDIT_ATHLETE_EXTRA), editAthlete);
        startActivity(addAthletePage);
        finish();
    }

    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
    }
}