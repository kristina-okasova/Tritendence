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
import android.widget.Toast;

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
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private Group editGroup;
    private String signedUser, sportSelection, theme;

    //Layout's items
    private ConstraintLayout trainingUnitLayout;
    private ListScrollable listOfAthletes, listOfTrainingUnits;
    private EditText nameOfGroup, placeOfTraining;
    private TextView timeInformation;
    private Spinner typeOfSport, dayOfTrainingUnit, categoryOfGroup;
    private ImageView addIcon;
    private Button addGroupBtn;

    private ArrayList<HashMap<String, Object>> dataForListOfTrainingUnits;
    private SimpleAdapter adapter;
    private int groupID;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        setContentView(R.layout.activity_add_group);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        this.moveLayoutWithKeyboard(navigation);

        //Setting currently selected navigation item and navigation listener.
        navigation.setSelectedItemId(R.id.groupsFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Getting extras of the intent and initializing items of the layout by their IDs.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.editGroup = (Group) getIntent().getExtras().getSerializable(getString(R.string.EDIT_GROUP_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.initializeItemsOfLayout();

        //Initializing spinners (days of the week, types of sports, categories of groups), list of all the athletes
        //and adapter for list of training units.
        this.dataForListOfTrainingUnits = new ArrayList<>();
        this.initializeTypeOfSport();
        this.initializeDayOfTheWeek();
        this.initializeCategoriesOfGroup();
        this.initializeAthletesOfClub();
        this.setAdapterForListOfTrainingUnits();

        //Fill the items of layout in case of editing an existing group.
        if (this.editGroup != null)
            this.fillGroupInformation();
    }

    private void moveLayoutWithKeyboard(BottomNavigationView navigation) {
        ScrollView scrollView = findViewById(R.id.addGroupScrollView);
        final View activityRootView = findViewById(R.id.addGroupActivity);

        //Changing margin of the whole layout when keyboard is shown. The change is caused by hidden navigation.
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight() - 2*navigation.getHeight();

            //When the keyboard is shown, the margin is set to zero.
            if (heightDiff > navigation.getHeight()) {
                navigation.setVisibility(View.GONE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                scrollView.setLayoutParams(params);
                scrollView.requestLayout();
            }

            //When the keyboard is hidden, the margin is restored.
            if (heightDiff < navigation.getHeight()) {
                navigation.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
                params.setMargins(0, 0, 0, 60);
                scrollView.setLayoutParams(params);
                scrollView.requestLayout();
            }
        });
    }

    private void initializeItemsOfLayout() {
        this.listOfTrainingUnits = findViewById(R.id.listOfTrainingUnits);
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
    }

    private void initializeTypeOfSport() {
        ArrayList<String> typesOfSport = new ArrayList<>();
        typesOfSport.add(getString(R.string.SWIMMING));
        typesOfSport.add(getString(R.string.ATHLETICS));
        typesOfSport.add(getString(R.string.CYCLING));
        typesOfSport.add(getString(R.string.STRENGTH));
        typesOfSport.add(getString(R.string.OTHER));

        //Setting adapter for types of sport's spinner.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, typesOfSport);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.typeOfSport.setAdapter(adapterSpinner);
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

        //Setting adapter for days of the week's spinner.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, daysOfTheWeek);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.dayOfTrainingUnit.setAdapter(adapterSpinner);
    }

    private void initializeCategoriesOfGroup() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getString(R.string.SPORTING_ATHLETES));
        categories.add(getString(R.string.START_SPORTING_ATHLETES));
        categories.add(getString(R.string.BEGINNER_ATHLETES));
        categories.add(getString(R.string.RECRE_ATHLETES));
        categories.add(getString(R.string.ADULT_ATHLETES));

        //Setting adapter for category's spinner.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.categoryOfGroup.setAdapter(adapterSpinner);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeAthletesOfClub() {
        ArrayList<String> namesOfAthletes = new ArrayList<>();
        for (Member athlete : this.club.getAthletesSortedByAlphabet())
            namesOfAthletes.add(athlete.getFullName());

        //Setting adapter for list of athletes.
        ArrayAdapter<String> adapterListView = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, namesOfAthletes);
        this.listOfAthletes.setAdapter(adapterListView);
    }

    private void setAdapterForListOfTrainingUnits() {
        String[] insertingData = {getString(R.string.DAY_AND_TIME_TRAINING), getString(R.string.TYPE_AND_PLACE_TRAINING)};
        int[] UIData = {R.id.dayAndTimeOfTrainingList, R.id.typeAndPlaceOfTrainingList};
        this.adapter = new SimpleAdapter(this, dataForListOfTrainingUnits, R.layout.training_unit_in_list_of_training_units, insertingData, UIData);
        listOfTrainingUnits.setAdapter(adapter);

    }

    private void fillGroupInformation() {
        this.nameOfGroup.setText(this.editGroup.getName());
        this.categoryOfGroup.setSelection(Integer.parseInt(this.editGroup.getCategory()) - 1);
        //Mark athletes that are already members of the editing group.
        for (Athlete athlete : this.editGroup.getAthletesOfGroup()) {
            for (int i = 0; i < this.listOfAthletes.getCount(); i++) {
                if (this.listOfAthletes.getItemAtPosition(i).toString().equals(athlete.getFullName()))
                    this.listOfAthletes.setItemChecked(i, true);
            }
        }

        //Displaying all the training units of the editing group.
        for (TrainingUnit unit : this.editGroup.getTimetable()) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.DAY_AND_TIME_TRAINING), unit.getDay() + " " + unit.getTime());
            mappedData.put(getString(R.string.TYPE_AND_PLACE_TRAINING), unit.getSportTranslation() + ", " + unit.getLocation());

            this.dataForListOfTrainingUnits.add(mappedData);
            this.adapter.notifyDataSetChanged();
        }

        this.addGroupBtn.setText(getString(R.string.CHANGE_GROUP_INFORMATION));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addTrainingUnit(View view) {
        //Showing layout for filling information about a new training unit.
        if (this.trainingUnitLayout.getVisibility() == View.GONE) {
            this.trainingUnitLayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                this.addIcon.setImageDrawable(getDrawable(R.drawable.confim_icon));
        }

        //Adding new training unit to the list and hiding the layout.
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
        //Checking if the training unit's time is filled in.
        if (this.timeInformation.getText().toString().equals(getString(R.string.EMPTY_STRING))) {
            Toast.makeText(this, getString(R.string.REQUIRED_TIME_OF_TRAINING), Toast.LENGTH_LONG).show();
            return;
        }

        //Checking if the training unit's place is filled in.
        if (this.placeOfTraining.getText().toString().equals(getString(R.string.EMPTY_STRING))) {
            Toast.makeText(this, getString(R.string.REQUIRED_LOCATION_OF_TRAINING), Toast.LENGTH_LONG).show();
            return;
        }

        //Getting data of the new training unit.
        HashMap<String, Object> mappedData = new HashMap<>();
        mappedData.put(getString(R.string.DAY_AND_TIME_TRAINING), this.dayOfTrainingUnit.getSelectedItem() + " " + this.timeInformation.getText().toString().trim());
        mappedData.put(getString(R.string.TYPE_AND_PLACE_TRAINING), this.typeOfSport.getSelectedItem() + ", " + this.placeOfTraining.getText().toString().trim());

        //Adding new training unit to the list of units.
        this.dataForListOfTrainingUnits.add(mappedData);
        this.adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createGroup(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        //Checking if the group's name is filled in.
        String name = this.nameOfGroup.getText().toString().trim();
        if (name.equals(getString(R.string.EMPTY_STRING))) {
            this.nameOfGroup.setError(getString(R.string.REQUIRED_NAME_OF_GROUP));
            return;
        }

        //Checking if the group has at least one training unit.
        if (this.dataForListOfTrainingUnits.size() == 0) {
            Toast.makeText(this, getString(R.string.TRAINING_UNIT_REQUIRED), Toast.LENGTH_LONG).show();
            return;
        }

        //Setting group's ID and category.
        int category = this.categoryOfGroup.getSelectedItemPosition() + 1;
        if (this.editGroup != null)
            this.groupID = this.editGroup.getID();
        else
            this.groupID = this.club.getNumberOfGroups() + 1;

        //Writing group's data to the database.
        root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.CATEGORY_DB)).setValue(category);
        root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.NAME_DB)).setValue(name);

        this.writeTrainingUnitsToDatabase();
        this.getAthletesOfGroup();
        //If the created group is the first one then delete the sign of the empty group's child.
        if (this.club.getNumberOfGroups() == 0)
            root.child(getString(R.string.GROUP_CHILD_DB) + "/" + getString(R.string.FIRST_CHILD_DB)).removeValue();
        this.loadGroupsPage();
    }

    private void writeTrainingUnitsToDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        int numberOfSwimmingTrainings = 0, numberOfAthleticTrainings = 0, numberOfCyclingTrainings = 0, numberOfStrengthTrainings = 0, numberOfOtherTrainings = 0;

        for (HashMap<String, Object> data : this.dataForListOfTrainingUnits) {
            //Extracting data of training unit.
            String firstPart = Objects.requireNonNull(data.get(getString(R.string.DAY_AND_TIME_TRAINING))).toString();
            String secondPart = Objects.requireNonNull(data.get(getString(R.string.TYPE_AND_PLACE_TRAINING))).toString();
            String day = firstPart.substring(0, firstPart.indexOf(' '));
            String time = firstPart.substring(firstPart.indexOf(' ') + 1);
            String sport = secondPart.substring(0, secondPart.indexOf(','));
            String location = secondPart.substring(secondPart.indexOf(',') + 2);

            //Finding root and ID of training unit based on the selected sport;
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

            //Writing timetable's data to the database.
            root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.TIMETABLE_DB) + "/" + timetableRoot + "/" + trainingUnitID + "/" + getString(R.string.DAY_DB)).setValue(day);
            root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.TIMETABLE_DB) + "/" + timetableRoot + "/" + trainingUnitID + "/" + getString(R.string.TIME_DB)).setValue(time);
            root.child(getString(R.string.GROUP_CHILD_DB) + "/" + this.groupID + "/" + getString(R.string.TIMETABLE_DB) + "/" + timetableRoot + "/" + trainingUnitID + "/" + getString(R.string.LOCATION_DB)).setValue(location);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAthletesOfGroup() {
        //Getting checked athletes from the list of all athletes.
        ArrayList<String> namesOfAthletesOfGroup = new ArrayList<>();
        for (int i = 0; i < this.listOfAthletes.getCount(); i++) {
            if (this.listOfAthletes.isItemChecked(i)) {
                String nameOfAthlete = this.listOfAthletes.getItemAtPosition(i).toString();
                namesOfAthletesOfGroup.add(nameOfAthlete);
            }
        }
        this.findAthletesByNames(namesOfAthletesOfGroup);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void findAthletesByNames(ArrayList<String> namesOfAthletesOfGroup) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        //Finding athletes based on the checked names from the list.
        for (String nameOfAthlete : namesOfAthletesOfGroup) {
            for (Member athlete : this.club.getAthletesSortedByAlphabet()) {
                //Setting athlete's group ID.
                if (athlete.getFullName().equals(nameOfAthlete)) {
                    ((Athlete) athlete).setGroupID(groupID);
                    root.child(getString(R.string.ATHLETES_CHILD_DB) + "/" + athlete.getID() + "/" + getString(R.string.GROUP_ID_DB)).setValue(groupID);
                }
            }
        }
    }

    private void loadGroupsPage() {
        //Creating new intent of Home Activity after creating new group.
        Intent groupsPage = new Intent(this, HomeActivity.class);
        groupsPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        groupsPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        groupsPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        groupsPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        groupsPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        groupsPage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), R.id.groupsFragment);
        startActivity(groupsPage);
        finish();
    }

    public void displayTimeSelection(View view) {
        DialogFragment timePicker;

        //If the time picker was used before, the set time remains.
        if (this.timeInformation.getText().length() != 0) {
            String time = this.timeInformation.getText().toString();
            int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
            int minute = Integer.parseInt(time.substring(time.indexOf(':') + 1));
            timePicker = new TimePicker(hour, minute);
        }
        //Otherwise is created time picker with current time.
        else
            timePicker = new TimePicker();

        timePicker.show(getSupportFragmentManager(), getString(R.string.TIME_PICKER_TAG));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        //Displaying selected time.
        this.timeInformation.setText(String.format("%02d:%02d", hourOfDay, minute));
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                //Creating new intent of Home Activity as part of navigation listener.
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), club);
                homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
                homePage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
                homePage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };
}