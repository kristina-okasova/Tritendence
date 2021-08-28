package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AddAthleteActivity extends AppCompatActivity {
    private static final String ATHLETES_CHILD_DATABASE = "Athletes";

    private TriathlonClub club;
    private EditText nameOfAthlete, surnameOfAthlete, emailOfAthlete;
    private Spinner groupOfAthlete;
    private TextView dayOfBirthOfAthlete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_athlete);

        this.club =  (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.nameOfAthlete = findViewById(R.id.nameOfAthleteCreate);
        this.surnameOfAthlete = findViewById(R.id.surnameOfAthleteCreate);
        this.emailOfAthlete = findViewById(R.id.emailOfAthleteCreate);
        this.groupOfAthlete = findViewById(R.id.groupOfAthlete);
        this.dayOfBirthOfAthlete = findViewById(R.id.dayOfBirthOfAthlete);

        this.initializeGroupsOfClub();
    }

    private void initializeGroupsOfClub() {
        ArrayList<Group> groupsOfClub = this.club.getGroupsOfClub();
        ArrayList<String> namesOfGroups = new ArrayList<>();
        for (Group group : groupsOfClub)
            namesOfGroups.add(group.getID() + ": " + group.getName());

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, namesOfGroups);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.groupOfAthlete.setAdapter(adapterSpinner);
    }

    public void createAthlete(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        String name = this.nameOfAthlete.getText().toString();
        if (name.equals(getString(R.string.EMPTY_STRING))) {
            this.nameOfAthlete.setError("Je potrebné zadať meno člena.");
            return;
        }

        String surname = this.surnameOfAthlete.getText().toString();
        if (surname.equals(getString(R.string.EMPTY_STRING))) {
            this.surnameOfAthlete.setError("Je potrebné zadať priezvisko člena.");
            return;
        }

        String email = this.emailOfAthlete.getText().toString();
        if (email.equals(getString(R.string.EMPTY_STRING))) {
            this.emailOfAthlete.setError("Je potrebné zadať e-mail člena.");
            return;
        }

        int athleteID = this.club.getNumberOfAthletes() + 1;
        String groupInformation = this.groupOfAthlete.getSelectedItem().toString();
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/Name").setValue(this.nameOfAthlete.getText().toString());
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/Surname").setValue(this.surnameOfAthlete.getText().toString());
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/E-Mail").setValue(this.emailOfAthlete.getText().toString());
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/GroupID").setValue(groupInformation.substring(0, groupInformation.indexOf(':')));
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/NumberOfTrainings").setValue(0);

        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        Intent athletesPage = new Intent(this, HomeActivity.class);
        athletesPage.putExtra("SIGNED_USER", signedUser);
        athletesPage.putExtra("TRIATHLON_CLUB", this.club);
        athletesPage.putExtra("SELECTED_FRAGMENT", R.id.athletesFragment);
        startActivity(athletesPage);
        finish();
    }

    @SuppressLint("DefaultLocale")
    public void displayDateSelection(View view) {
        DatePickerDialog.OnDateSetListener setListener = null;

        setListener = (view1, year, month, dayOfMonth) -> {
            this.dayOfBirthOfAthlete.setText(String.format("Dátum narodenia: %02d.%02d.%d", dayOfMonth, month, year));
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }
}