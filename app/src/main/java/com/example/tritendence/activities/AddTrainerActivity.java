package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTrainerActivity extends AppCompatActivity {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String signedUser, sportSelection, theme;
    private Trainer editTrainer;

    //Layout's items
    private EditText nameOfTrainer, surnameOfTrainer;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swimmingSwitch, athleticsSwitch, cyclingSwitch;
    private String sportText;

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
        setContentView(R.layout.activity_add_trainer);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);

        //Setting currently selected navigation item and navigation listener.
        navigation.setSelectedItemId(R.id.athletesFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Getting extras of the intent and initializing items of the layout by their IDs.
        this.club =  (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.editTrainer = (Trainer) getIntent().getExtras().getSerializable(getString(R.string.EDIT_TRAINER_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.initializeItemsOfLayout();

        //Setting listeners on switch buttons to set currently selected type of sport.
        this.setListenerForSwitch(this.swimmingSwitch, this.athleticsSwitch, this.cyclingSwitch, getString(R.string.SWIMMING));
        this.setListenerForSwitch(this.athleticsSwitch, this.swimmingSwitch, this.cyclingSwitch, getString(R.string.ATHLETICS));
        this.setListenerForSwitch(this.cyclingSwitch, this.swimmingSwitch, this.athleticsSwitch, getString(R.string.CYCLING));

        //Fill the items of layout in case of editing an existing athlete.
        if (this.editTrainer != null)
            this.fillTrainerInformation();
    }

    private void initializeItemsOfLayout() {
        this.nameOfTrainer = findViewById(R.id.nameOfTrainerCreate);
        this.surnameOfTrainer = findViewById(R.id.surnameOfTrainerCreate);
        this.swimmingSwitch = findViewById(R.id.trainersSwimmingSwitch);
        this.athleticsSwitch = findViewById(R.id.trainersAthleticsSwitch);
        this.cyclingSwitch = findViewById(R.id.trainersCyclingSwitch);
    }

    private void setListenerForSwitch(@SuppressLint("UseSwitchCompatOrMaterialCode") Switch firstSwitch, @SuppressLint("UseSwitchCompatOrMaterialCode") Switch secondSwitch, @SuppressLint("UseSwitchCompatOrMaterialCode") Switch thirdSwitch, String sport) {
        firstSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                secondSwitch.setChecked(false);
                thirdSwitch.setChecked(false);
                this.sportText = sport;
            }
            else
                this.sportText = getString(R.string.EMPTY_STRING);
        });
    }

    private void fillTrainerInformation() {
        this.nameOfTrainer.setText(this.editTrainer.getName());
        this.surnameOfTrainer.setText(this.editTrainer.getSurname());

        if (this.editTrainer.getSport().equals(getString(R.string.SWIMMING)))
            this.swimmingSwitch.setChecked(true);
        if (this.editTrainer.getSport().equals(getString(R.string.ATHLETICS)))
            this.athleticsSwitch.setChecked(true);
        if (this.editTrainer.getSport().equals(getString(R.string.CYCLING)))
            this.cyclingSwitch.setChecked(true);
    }

    public void createTrainer(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        String name = this.nameOfTrainer.getText().toString().trim();
        String surname = this.surnameOfTrainer.getText().toString().trim();

        //Checking if the trainer's name and surname are filled in.
        if (this.checkExistenceOfData(this.nameOfTrainer, name, getString(R.string.REQUIRED_NAME_OF_ATHLETE)) ||
                this.checkExistenceOfData(this.surnameOfTrainer, surname, getString(R.string.REQUIRED_SURNAME_OF_ATHLETE)))
            return;

        //Checking if the trainer to be created is already a member of the club.
        if (!this.checkPossibilityToAddAthlete(name, surname)) {
            Toast.makeText(this, getString(R.string.ATHLETE_ALREADY_EXISTS), Toast.LENGTH_LONG).show();
            return;
        }

        if (this.sportText.equals(getString(R.string.EMPTY_STRING))) {
            Toast.makeText(this, getString(R.string.NO_SPORT_SELECTED), Toast.LENGTH_LONG).show();
            return;
        }

        //Setting trainer's ID, numberOfTrainings, email and theme.
        int trainerID, numberOfTrainings;
        String email, theme;
        if (this.editTrainer != null) {
            trainerID = this.editTrainer.getID();
            email = this.editTrainer.getEmail();
            theme = this.editTrainer.getTheme();
        }
        else {
            trainerID = this.club.getNumberOfTrainers() + 1;
            email = "null";
            theme = "DarkRed";
        }

        //Writing trainer's data to the database.
        root.child(getString(R.string.TRAINERS_CHILD_DB) + "/" + trainerID + "/" + getString(R.string.NAME_DB)).setValue(name);
        root.child(getString(R.string.TRAINERS_CHILD_DB) + "/" + trainerID + "/" + getString(R.string.SURNAME_DB)).setValue(surname);
        root.child(getString(R.string.TRAINERS_CHILD_DB) + "/" + trainerID + "/" + getString(R.string.EMAIL)).setValue(email);
        root.child(getString(R.string.TRAINERS_CHILD_DB) + "/" + trainerID + "/" + getString(R.string.THEME_DB)).setValue(theme);
        root.child(getString(R.string.TRAINERS_CHILD_DB) + "/" + trainerID + "/" + getString(R.string.SPORT_DB)).setValue(this.sportText);

        this.loadTrainersPage();
    }

    private boolean checkExistenceOfData(EditText editText, String data, String value) {
        if (data.equals(getString(R.string.EMPTY_STRING))) {
            editText.setError(value);
            return true;
        }
        return false;
    }

    private boolean checkPossibilityToAddAthlete(String name, String surname) {
        if (this.editTrainer != null)
            return true;
        for (Member athlete : this.club.getMembersOfClub()) {
            if (athlete.getName().equals(name) && athlete.getSurname().equals(surname))
                return false;
        }
        return true;
    }

    private void loadTrainersPage() {
        //Creating new intent of Home Activity after creating new athlete.
        Intent athletesPage = new Intent(this, HomeActivity.class);
        athletesPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        athletesPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        athletesPage.putExtra(getString(R.string.LOAD_DATA_EXTRA),this. loadData);
        athletesPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        athletesPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        athletesPage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), R.id.trainersFragment);
        startActivity(athletesPage);
        finish();
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
                homePage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };
}