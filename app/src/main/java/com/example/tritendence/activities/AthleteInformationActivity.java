package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.tritendence.R.*;

public class AthleteInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_information);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(id.athletesFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        AthleteInformationFragment athleteInformationFragment = new AthleteInformationFragment(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.athleteFragmentContainerView, athleteInformationFragment).commit();
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
}