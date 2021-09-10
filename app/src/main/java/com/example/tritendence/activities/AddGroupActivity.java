package com.example.tritendence.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.lists.ListScrollable;
import com.example.tritendence.model.TimePicker;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddGroupActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private final static String GROUPS_CHILD_DATABASE = "Groups";
    private final static String ATHLETES_CHILD_DATABASE = "Athletes";

    private TriathlonClub club;
    private Group editGroup;
    private ListScrollable listOfAthletes;
    private ConstraintLayout trainingUnitLayout;
    private EditText nameOfGroup, placeOfTraining;
    private TextView timeInformation;
    private Spinner typeOfSport, dayOfTrainingUnit, categoryOfGroup;
    private ImageView addIcon;
    private ArrayList<HashMap<String, Object>> dataForListOfTrainingUnits;
    private SimpleAdapter adapter;
    private Button addGroupBtn;
    private int groupID;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        ScrollView scrollView = findViewById(R.id.addGroupScrollView);
        final View activityRootView = findViewById(R.id.addGroupActivity);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight() - 2*navigation.getHeight();

            if (heightDiff > navigation.getHeight()) {
                navigation.setVisibility(View.GONE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                scrollView.setLayoutParams(params);
                scrollView.requestLayout();
            }

            if (heightDiff < navigation.getHeight()) {
                navigation.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
                params.setMargins(0, 0, 0, 60);
                scrollView.setLayoutParams(params);
                scrollView.requestLayout();
            }
        });
        navigation.setSelectedItemId(R.id.groupsFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.editGroup = (Group) getIntent().getExtras().getSerializable(getString(R.string.EDIT_GROUP_EXTRA));
        ListScrollable listOfTrainingUnits = findViewById(R.id.listOfTrainingUnits);
        this.listOfAthletes = findViewById(R.id.listOfAthletesOfClub);
        this.trainingUnitLayout = findViewById(R.id.addTrainingUnit);
        this.nameOfGroup = findViewById(R.id.nameOfGroup);
        this.categoryOfGroup = findViewById(R.id.categoryOfGroup);
        this.typeOfSport = findViewById(R.id.sportOfTraining);
        this.dayOfTrainingUnit = findViewById(R.id.dayOfTraining);
        this.timeInformation = findViewById(R.id.timeOfTrainingInformation);
        this.placeOfTraining = findViewById(R.id.placeOfTraining);
        this.addIcon = findViewById(R.id.addIcon);
        this.addGroupBtn = findViewById(R.id.addGroupBtn);
        this.dataForListOfTrainingUnits = new ArrayList<>();
        this.initializeTypeOfSport();
        this.initializeDayOfTheWeek();
        this.initializeAthletesOfClub();
        this.initializeCategoriesOfGroup();

        String[] insertingData = {getString(R.string.DAY_AND_TIME_TRAINING), getString(R.string.TYPE_AND_PLACE_TRAINING)};
        int[] UIData = {R.id.dayAndTimeOfTrainingList, R.id.typeAndPlaceOfTrainingList};
        this.adapter = new SimpleAdapter(this, dataForListOfTrainingUnits, R.layout.training_unit_in_list_of_training_units, insertingData, UIData);
        listOfTrainingUnits.setAdapter(adapter);

        if (this.editGroup != null)
            this.fillGroupInformation();
    }

    private void fillGroupInformation() {
        this.nameOfGroup.setText(this.editGroup.getName());
        this.categoryOfGroup.setSelection(Integer.parseInt(this.editGroup.getCategory()) - 1);
        for (Athlete athlete : this.editGroup.getAthletesOfGroup()) {
            for (int i = 0; i < this.listOfAthletes.getCount(); i++) {
                if (this.listOfAthletes.getItemAtPosition(i).toString().equals(athlete.getFullName()))
                    this.listOfAthletes.setItemChecked(i, true);
            }
        }

        for (TrainingUnit unit : this.editGroup.getTimetable()) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.DAY_AND_TIME_TRAINING), unit.getDay() + " " + unit.getTime());
            mappedData.put(getString(R.string.TYPE_AND_PLACE_TRAINING), unit.getSportTranslation() + ", " + unit.getLocation());

            this.dataForListOfTrainingUnits.add(mappedData);
            this.adapter.notifyDataSetChanged();
        }

        this.addGroupBtn.setText(getString(R.string.CHANGE_GROUP_INFORMATION));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    private void initializeCategoriesOfGroup() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getString(R.string.SPORTING_ATHLETES));
        categories.add(getString(R.string.START_SPORTING_ATHLETES));
        categories.add(getString(R.string.BEGINNER_ATHLETES));

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.categoryOfGroup.setAdapter(adapterSpinner);
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
            this.timeInformation.setError(getString(R.string.REQUIRED_TIME_OF_TRAINING));
            return;
        }

        if (this.placeOfTraining.getText().toString().equals(getString(R.string.EMPTY_STRING))) {
            this.placeOfTraining.setError(getString(R.string.REQUIRED_LOCATION_OF_TRAINING));
            return;
        }

        HashMap<String, Object> mappedData = new HashMap<>();

        mappedData.put(getString(R.string.DAY_AND_TIME_TRAINING), this.dayOfTrainingUnit.getSelectedItem() + " " + this.timeInformation.getText().toString().trim());
        mappedData.put(getString(R.string.TYPE_AND_PLACE_TRAINING), this.typeOfSport.getSelectedItem() + ", " + this.placeOfTraining.getText().toString().trim());

        this.dataForListOfTrainingUnits.add(mappedData);
        this.adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createGroup(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        int numberOfSwimmingTrainings = 0, numberOfAthleticTrainings = 0, numberOfCyclingTrainings = 0, numberOfStrengthTrainings = 0, numberOfOtherTrainings = 0;

        String name = this.nameOfGroup.getText().toString().trim();
        if (name.equals(getString(R.string.EMPTY_STRING))) {
            this.nameOfGroup.setError(getString(R.string.REQUIRED_NAME_OF_GROUP));
            return;
        }
        int category = this.categoryOfGroup.getSelectedItemPosition() + 1;

        if (this.editGroup != null)
            this.groupID = this.editGroup.getID();
        else
            this.groupID = this.club.getNumberOfGroups() + 1;
        root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.CATEGORY_DB)).setValue(category);
        root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.NAME_DB)).setValue(name);

        for (HashMap<String, Object> data : this.dataForListOfTrainingUnits) {
            String firstPart = Objects.requireNonNull(data.get(getString(R.string.DAY_AND_TIME_TRAINING))).toString();
            String secondPart = Objects.requireNonNull(data.get(getString(R.string.TYPE_AND_PLACE_TRAINING))).toString();
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
                case "Sila":
                    timetableRoot = getString(R.string.STRENGTH_DB);
                    trainingUnitID = ++numberOfStrengthTrainings;
                    break;
                case "Iné":
                    timetableRoot = getString(R.string.OTHER_DB);
                    trainingUnitID = ++numberOfOtherTrainings;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + sport);
            }

            root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.TIMETABLE_DB) + "/" + timetableRoot + "/" + trainingUnitID + "/" + getString(R.string.DAY_DB)).setValue(day);
            root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.TIMETABLE_DB) + "/" + timetableRoot + "/" + trainingUnitID + "/" + getString(R.string.TIME_DB)).setValue(time);
            root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.TIMETABLE_DB) + "/" + timetableRoot + "/" + trainingUnitID + "/" + getString(R.string.LOCATION_DB)).setValue(location);
        }

        ArrayList<String> namesOfAthletesOfGroup = new ArrayList<>();
        for (int i = 0; i < this.listOfAthletes.getCount(); i++) {
            if (this.listOfAthletes.isItemChecked(i)) {
                String nameOfAthlete = this.listOfAthletes.getItemAtPosition(i).toString();
                namesOfAthletesOfGroup.add(nameOfAthlete);
            }
        }
        this.findAthletesByNames(namesOfAthletesOfGroup);

        String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        Intent groupsPage = new Intent(this, HomeActivity.class);
        groupsPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        groupsPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        groupsPage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), R.id.groupsFragment);
        startActivity(groupsPage);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void findAthletesByNames(ArrayList<String> namesOfAthletesOfGroup) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        for (String nameOfAthlete : namesOfAthletesOfGroup) {
            for (Member athlete : this.club.getAthletesSortedByAlphabet()) {
                if (athlete.getFullName().equals(nameOfAthlete)) {
                    ((Athlete) athlete).setGroupID(groupID);
                    root.child(ATHLETES_CHILD_DATABASE + "/" + athlete.getID() + "/" + getString(R.string.GROUP_ID_DB)).setValue(groupID);
                }
            }
        }
    }

    public void displayTimeSelection(View view) {
        DialogFragment timePicker;
        if (this.timeInformation.getText().length() != 0) {
            String time = this.timeInformation.getText().toString();
            int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
            int minute = Integer.parseInt(time.substring(time.indexOf(':') + 1));
            timePicker = new TimePicker(hour, minute);
        }
        else
            timePicker = new TimePicker();
        timePicker.show(getSupportFragmentManager(), getString(R.string.TIME_PICKER_TAG));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        this.timeInformation.setText(String.format("%02d:%02d", hourOfDay, minute));
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                TriathlonClub club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
                String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
                LoadData loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), club);
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