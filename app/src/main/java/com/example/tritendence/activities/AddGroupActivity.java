package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.ListScrollable;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TimePicker;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddGroupActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private final static String GROUPS_CHILD_DATABASE = "Groups";
    private final static String ATHLETES_CHILD_DATABASE = "Athletes";

    private TriathlonClub club;
    private ListScrollable listOfTrainingUnits, listOfAthletes;
    private ConstraintLayout trainingUnitLayout;
    private EditText nameOfGroup, placeOfTraining;
    private TextView timeInformation;
    private Spinner typeOfSport, dayOfTrainingUnit;
    private ImageView timeIcon, addIcon;
    private ArrayList<HashMap<String, Object>> dataForListOfTrainingUnits;
    private SimpleAdapter adapter;
    private int groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.listOfTrainingUnits = findViewById(R.id.listOfTrainingUnits);
        this.listOfAthletes = findViewById(R.id.listOfAthletesOfClub);
        this.trainingUnitLayout = findViewById(R.id.addTrainingUnit);
        this.nameOfGroup = findViewById(R.id.nameOfGroup);
        this.typeOfSport = findViewById(R.id.sportOfTraining);
        this.dayOfTrainingUnit = findViewById(R.id.dayOfTraining);
        this.timeInformation = findViewById(R.id.timeOfTrainingInformation);
        this.timeIcon = findViewById(R.id.timeOfTrainingUnitIcon);
        this.placeOfTraining = findViewById(R.id.placeOfTraining);
        this.addIcon = findViewById(R.id.addIcon);
        this.dataForListOfTrainingUnits = new ArrayList<>();
        this.initializeTypeOfSport();
        this.initializeDayOfTheWeek();
        this.initializeAthletesOfClub();

        String[] insertingData = {"dayAndTimeOfTraining", "typeAndPlaceOfTraining"};
        int[] UIData = {R.id.dayAndTimeOfTrainingList, R.id.typeAndPlaceOfTrainingList};
        this.adapter = new SimpleAdapter(this, dataForListOfTrainingUnits, R.layout.training_unit_in_list_of_training_units, insertingData, UIData);
        this.listOfTrainingUnits.setAdapter(adapter);

    }

    private void initializeTypeOfSport() {
        ArrayList<String> typesOfSport = new ArrayList<>();
        typesOfSport.add(getString(R.string.SWIMMING));
        typesOfSport.add(getString(R.string.ATHLETICS));
        typesOfSport.add(getString(R.string.CYCLING));
        typesOfSport.add(getString(R.string.STRENGTH));
        typesOfSport.add(getString(R.string.OTHER));

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, typesOfSport);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.typeOfSport.setAdapter(adapterSpinner);
    }

    private void initializeAthletesOfClub() {
        ArrayList<String> namesOfAthletes = new ArrayList<>();
        for (Member athlete : this.club.getAthletesSortedByAlphabet())
            namesOfAthletes.add(athlete.getFullName());

        ArrayAdapter<String> adapterListView = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, namesOfAthletes);
        this.listOfAthletes.setAdapter(adapterListView);
    }

    private void initializeDayOfTheWeek() {
        ArrayList<String> daysOfTheWeek = new ArrayList<>();
        daysOfTheWeek.add(getString(R.string.MONDAY));
        daysOfTheWeek.add(getString(R.string.TUESDAY));
        daysOfTheWeek.add(getString(R.string.WEDNESDAY));
        daysOfTheWeek.add(getString(R.string.THURSDAY));
        daysOfTheWeek.add(getString(R.string.FRIDAY));
        daysOfTheWeek.add(getString(R.string.SATURDAY));
        daysOfTheWeek.add(getString(R.string.SUNDAY));

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, daysOfTheWeek);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.dayOfTrainingUnit.setAdapter(adapterSpinner);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addTrainingUnit(View view) {
        if (this.trainingUnitLayout.getVisibility() == View.GONE) {
            this.trainingUnitLayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                this.addIcon.setImageDrawable(getDrawable(R.drawable.confim_icon));
        }

        else {
            this.addTrainingUnitToList();
            this.typeOfSport.setSelection(0);
            this.placeOfTraining.setText(getString(R.string.EMPTY_STRING));
            this.dayOfTrainingUnit.setSelection(0);
            this.trainingUnitLayout.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                this.addIcon.setImageDrawable(getDrawable(R.drawable.add_circle_icon));
        }
    }

    private void addTrainingUnitToList() {
        if (this.timeInformation.getText().toString().equals(getString(R.string.EMPTY_STRING))) {
            this.timeInformation.setError("Je potrebné zvoliť čas tréningu.");
            return;
        }

        if (this.placeOfTraining.getText().toString().equals(getString(R.string.EMPTY_STRING))) {
            this.placeOfTraining.setError("Je potrebné zvoliť miesto tréningu.");
            return;
        }

        HashMap<String, Object> mappedData = new HashMap<>();

        mappedData.put("dayAndTimeOfTraining", this.dayOfTrainingUnit.getSelectedItem() + " " + this.timeInformation.getText());
        mappedData.put("typeAndPlaceOfTraining", this.typeOfSport.getSelectedItem() + ", " + this.placeOfTraining.getText());

        this.dataForListOfTrainingUnits.add(mappedData);
        this.adapter.notifyDataSetChanged();
    }

    public void createGroup(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        int numberOfSwimmingTrainings = 0, numberOfAthleticTrainings = 0, numberOfCyclingTrainings = 0;

        String name = this.nameOfGroup.getText().toString();
        if (name.equals(getString(R.string.EMPTY_STRING))) {
            this.nameOfGroup.setError("Skupina musí mať názov.");
            return;
        }

        this.groupID = this.club.getNumberOfGroups() + 1;
        root.child(GROUPS_CHILD_DATABASE + "/" + this.groupID + "/Category").setValue(1);
        root.child(GROUPS_CHILD_DATABASE + "/" + this.groupID + "/Name").setValue(name);

        for (HashMap<String, Object> data : this.dataForListOfTrainingUnits) {
            String firstPart = Objects.requireNonNull(data.get("dayAndTimeOfTraining")).toString();
            String secondPart = Objects.requireNonNull(data.get("typeAndPlaceOfTraining")).toString();
            String day = firstPart.substring(0, firstPart.indexOf(' '));
            String time = firstPart.substring(firstPart.indexOf(' ') + 1);
            String sport = secondPart.substring(0, secondPart.indexOf(','));
            String location = secondPart.substring(secondPart.indexOf(',') + 2);

            String timetableRoot;
            int trainingUnitID;
            switch(sport) {
                case "Plávanie":
                    timetableRoot = getString(R.string.SWIMMING_DB);
                    trainingUnitID = ++numberOfSwimmingTrainings;
                    break;
                case "Atletika":
                    timetableRoot = getString(R.string.ATHLETICS_DB);
                    trainingUnitID = ++numberOfAthleticTrainings;
                    break;
                case "Cyklistika":
                    timetableRoot = getString(R.string.CYCLING_DB);
                    trainingUnitID = ++numberOfCyclingTrainings;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + sport);
            }

            root.child(GROUPS_CHILD_DATABASE + "/" + this.groupID + "/Timetable/" + timetableRoot + "/" + trainingUnitID + "/day").setValue(day);
            root.child(GROUPS_CHILD_DATABASE + "/" + this.groupID + "/Timetable/" + timetableRoot + "/" + trainingUnitID + "/time").setValue(time);
            root.child(GROUPS_CHILD_DATABASE + "/" + this.groupID + "/Timetable/" + timetableRoot + "/" + trainingUnitID + "/location").setValue(location);
        }

        ArrayList<String> namesOfAthletesOfGroup = new ArrayList<>();
        for (int i = 0; i < this.listOfAthletes.getCount(); i++) {
            if (this.listOfAthletes.isItemChecked(i)) {
                String nameOfAthlete = this.listOfAthletes.getItemAtPosition(i).toString();
                namesOfAthletesOfGroup.add(nameOfAthlete);
            }
        }
        this.findAthletesByNames(namesOfAthletesOfGroup);

        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        Intent groupsPage = new Intent(this, HomeActivity.class);
        groupsPage.putExtra("SIGNED_USER", signedUser);
        groupsPage.putExtra("TRIATHLON_CLUB", this.club);
        groupsPage.putExtra("SELECTED_FRAGMENT", R.id.groupsFragment);
        startActivity(groupsPage);
        finish();
    }

    private void findAthletesByNames(ArrayList<String> namesOfAthletesOfGroup) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        for (String nameOfAthlete : namesOfAthletesOfGroup) {
            for (Member athlete : this.club.getAthletesSortedByAlphabet()) {
                if (athlete.getFullName().equals(nameOfAthlete)) {
                    ((Athlete) athlete).setGroupID(groupID);
                    root.child(ATHLETES_CHILD_DATABASE + "/" + athlete.getID() + "/GroupID").setValue(groupID);
                }
            }
        }
    }

    public void displayTimeSelection(View view) {
        DialogFragment timePicker = null;
        if (this.timeInformation.getText().length() != 0) {
            String time = this.timeInformation.getText().toString();
            int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
            int minute = Integer.parseInt(time.substring(time.indexOf(':') + 1));
            timePicker = new TimePicker(hour, minute);
        }
        else
            timePicker = new TimePicker();
        timePicker.show(getSupportFragmentManager(), "Time Picker");
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        this.timeInformation.setText(String.format("%02d:%02d", hourOfDay, minute));
    }
}