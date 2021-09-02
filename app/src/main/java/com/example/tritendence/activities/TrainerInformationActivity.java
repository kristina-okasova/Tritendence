package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.tritendence.R;
import com.example.tritendence.fragments.TrainerInformationFragment;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TrainerInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_information);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.trainersFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        TrainerInformationFragment trainerInformationFragment = new TrainerInformationFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.trainerFragmentContainerView, trainerInformationFragment).commit();
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