package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthleteInformationFragment;
import com.example.tritendence.fragments.AthletesFragment;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.GroupInformationFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
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

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(id.athletesFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.athleteInformationFragment = new AthleteInformationFragment(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.athleteFragmentContainerView, this.athleteInformationFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                String signedUser = getIntent().getExtras().getString("SIGNED_USER");
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra("SIGNED_USER", signedUser);
                homePage.putExtra("TRIATHLON_CLUB", this.club);
                homePage.putExtra("SELECTED_FRAGMENT", item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void deleteAthlete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Odstránenie športovca");
        builder.setMessage("Ste si istí, že chcete odstrániť daného športovca?");

        builder.setPositiveButton("Áno",
                (dialog, which) -> this.athleteInformationFragment.deleteAthlete());
        builder.setNegativeButton("Nie", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void editAthlete(View view) {
        Athlete editAthlete = this.athleteInformationFragment.findSelectedAthlete();
        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        Intent addAthletePage = new Intent(this, AddAthleteActivity.class);
        addAthletePage.putExtra("TRIATHLON_CLUB", this.club);
        addAthletePage.putExtra("SIGNED_USER", signedUser);
        addAthletePage.putExtra("EDIT_ATHLETE", editAthlete);
        startActivity(addAthletePage);
        finish();
    }
}