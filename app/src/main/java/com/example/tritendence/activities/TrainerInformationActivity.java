package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.tritendence.R;
import com.example.tritendence.fragments.TrainerInformationFragment;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TrainerInformationActivity extends AppCompatActivity {
    private TriathlonClub club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_information);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.trainersFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        TrainerInformationFragment trainerInformationFragment = new TrainerInformationFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.trainerFragmentContainerView, trainerInformationFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
                LoadData loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
                homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), loadData);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
    }
}