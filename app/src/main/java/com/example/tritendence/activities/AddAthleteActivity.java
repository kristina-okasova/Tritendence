package com.example.tritendence.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class AddAthleteActivity extends AppCompatActivity {
    private static final String ATHLETES_CHILD_DATABASE = "Athletes";

    private TriathlonClub club;
    private EditText nameOfAthlete, surnameOfAthlete;
    private Spinner groupOfAthlete;
    private TextView dayOfBirthOfAthlete;
    private Athlete editAthlete;
    private Button addAthleteBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_athlete);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.athletesFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        this.club =  (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.editAthlete = (Athlete) getIntent().getExtras().getSerializable("EDIT_ATHLETE");
        this.nameOfAthlete = findViewById(R.id.nameOfAthleteCreate);
        this.surnameOfAthlete = findViewById(R.id.surnameOfAthleteCreate);
        this.groupOfAthlete = findViewById(R.id.groupOfAthlete);
        this.dayOfBirthOfAthlete = findViewById(R.id.dayOfBirthOfAthlete);
        this.addAthleteBtn = findViewById(R.id.addAthleteBtn);

        this.initializeGroupsOfClub();
        if (this.editAthlete != null)
            this.fillAthleteInformation();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillAthleteInformation() {
        this.nameOfAthlete.setText(this.editAthlete.getName());
        this.surnameOfAthlete.setText(this.editAthlete.getSurname());
        this.groupOfAthlete.setSelection(this.editAthlete.getGroupID());
        this.dayOfBirthOfAthlete.setText(String.format("%s: %s", getString(R.string.DAY_OF_BIRTH), this.editAthlete.getDayOfBirth()));
        this.addAthleteBtn.setText(getString(R.string.CHANGE_ATHLETE_INFORMATION));
    }

    private void initializeGroupsOfClub() {
        ArrayList<Group> groupsOfClub = this.club.getGroupsOfClub();
        ArrayList<String> namesOfGroups = new ArrayList<>();
        namesOfGroups.add("--nezaradený--");
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

        int athleteID;
        if (this.editAthlete != null)
            athleteID = this.editAthlete.getID();
        else
            athleteID = this.club.getNumberOfAthletes() + 1;
        String groupID = String.valueOf(this.groupOfAthlete.getSelectedItemPosition());
        String dayOfBirth = this.dayOfBirthOfAthlete.getText().toString();
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/Name").setValue(this.nameOfAthlete.getText().toString().trim());
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/Surname").setValue(this.surnameOfAthlete.getText().toString().trim());
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/GroupID").setValue(groupID);
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/NumberOfTrainings").setValue(0);
        root.child(ATHLETES_CHILD_DATABASE + "/" + athleteID + "/DayOfBirth").setValue(dayOfBirth.substring(dayOfBirth.indexOf(':') + 2));

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
        DatePickerDialog.OnDateSetListener setListener;

        setListener = (view1, year, month, dayOfMonth) -> this.dayOfBirthOfAthlete.setText(String.format("Dátum narodenia: %02d.%02d.%d", dayOfMonth, month, year));

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
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