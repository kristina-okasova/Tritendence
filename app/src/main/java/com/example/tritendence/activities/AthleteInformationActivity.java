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
    //Intent's extras
    private TriathlonClub club;
    private String signedUser, sportSelection, theme;
    private LoadData loadData;

    private AthleteInformationFragment athleteInformationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting user's theme.
        this.theme = getIntent().getExtras().getString(getString(R.string.THEME_EXTRA));
        switch(this.theme) {
            case "DarkRed":
                setTheme(R.style.Theme_DarkRed);
                break;
            case "DarkBlue":
                setTheme(R.style.Theme_DarkBlue);
                break;
            case "LightRed":
                setTheme(R.style.Theme_LightRed);
                break;
            case "LightBlue":
                setTheme(R.style.Theme_LightBlue);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_information);

        //Getting extras of the intent.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(string.SIGNED_USER_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(string.LOAD_DATA_EXTRA));

        //Setting currently selected navigation item and navigation listener.
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(id.athletesFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Attaching fragment to the activity.
        this.athleteInformationFragment = new AthleteInformationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.athleteFragmentContainerView, this.athleteInformationFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                //Creating new intent of Home Activity as part of navigation listener.
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(string.SIGNED_USER_EXTRA), this.signedUser);
                homePage.putExtra(getString(string.TRIATHLON_CLUB_EXTRA), this.club);
                homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
                homePage.putExtra(getString(string.SPORT_SELECTION_EXTRA), this.sportSelection);
                homePage.putExtra(getString(string.THEME_EXTRA), this.theme);
                homePage.putExtra(getString(string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteAthlete(View view) {
        //Showing alert box to confirm deletion of the athlete.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(string.DELETE_ATHLETE));
        builder.setMessage(getString(string.DELETE_ATHLETE_QUESTION));

        //Deleting athlete when confirmed. Closing alert box when denied.
        builder.setPositiveButton(getString(string.YES),
                (dialog, which) -> this.athleteInformationFragment.deleteAthlete());
        builder.setNegativeButton(getString(string.NO), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void editAthlete(View view) {
        //Creating new intent of Add Athlete Activity when clicked on the edit icon.
        Athlete editAthlete = this.athleteInformationFragment.findAthleteByName();
        Intent addAthletePage = new Intent(this, AddAthleteActivity.class);
        addAthletePage.putExtra(getString(string.TRIATHLON_CLUB_EXTRA), this.club);
        addAthletePage.putExtra(getString(string.SIGNED_USER_EXTRA), this.signedUser);
        addAthletePage.putExtra(getString(string.LOAD_DATA_EXTRA), this.loadData);
        addAthletePage.putExtra(getString(string.SPORT_SELECTION_EXTRA), this.sportSelection);
        addAthletePage.putExtra(getString(string.THEME_EXTRA), this.theme);
        addAthletePage.putExtra(getString(string.EDIT_ATHLETE_EXTRA), editAthlete);
        startActivity(addAthletePage);
        finish();
    }
}