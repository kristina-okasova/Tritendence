package com.example.tritendence.activities;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.MaterialColors;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class AddAthleteActivity extends AppCompatActivity {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String signedUser, sportSelection, theme;
    private Athlete editAthlete;

    //Layout's items
    private EditText nameOfAthlete, surnameOfAthlete;
    private Spinner groupOfAthlete;
    private TextView dayOfBirthOfAthlete;
    private Button addAthleteBtn;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        setContentView(R.layout.activity_add_athlete);

        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        this.moveLayoutWithKeyboard(navigation);

        //Setting currently selected navigation item and navigation listener.
        navigation.setSelectedItemId(R.id.athletesFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Getting extras of the intent and initializing items of the layout by their IDs.
        this.club =  (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.editAthlete = (Athlete) getIntent().getExtras().getSerializable(getString(R.string.EDIT_ATHLETE_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.initializeItemsOfLayout();
        this.initializeGroupsOfClub();

        //Fill the items of layout in case of editing an existing athlete.
        if (this.editAthlete != null)
            this.fillAthleteInformation();
    }

    private void moveLayoutWithKeyboard(BottomNavigationView navigation) {
        ConstraintLayout constraintLayout = findViewById(R.id.addAthleteConstraintView);
        final View activityRootView = findViewById(R.id.addAthleteActivity);

        //Changing margin of the whole layout when keyboard is shown. The change is caused by hidden navigation.
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight() - 2*navigation.getHeight();

            //When the keyboard is shown, the margin is set to zero.
            if (heightDiff > navigation.getHeight()) {
                navigation.setVisibility(View.GONE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                constraintLayout.setLayoutParams(params);
                constraintLayout.requestLayout();
            }

            //When the keyboard is hidden, the margin is restored.
            if (heightDiff < navigation.getHeight()) {
                navigation.setVisibility(View.VISIBLE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintLayout.getLayoutParams();
                params.setMargins(0, 0, 0, 60);
                constraintLayout.setLayoutParams(params);
                constraintLayout.requestLayout();
            }
        });
    }

    private void initializeItemsOfLayout() {
        this.nameOfAthlete = findViewById(R.id.nameOfAthleteCreate);
        this.surnameOfAthlete = findViewById(R.id.surnameOfAthleteCreate);
        this.groupOfAthlete = findViewById(R.id.groupOfAthlete);
        this.dayOfBirthOfAthlete = findViewById(R.id.dayOfBirthOfAthlete);
        this.addAthleteBtn = findViewById(R.id.addAthleteBtn);
    }

    private void initializeGroupsOfClub() {
        //Adding names of groups to the group selection's spinner.
        ArrayList<String> namesOfGroups = new ArrayList<>();
        namesOfGroups.add(getString(R.string.GROUP_NOT_ASSIGNED));
        for (Group group : this.club.getGroupsOfClub())
            namesOfGroups.add(group.getID() + ": " + group.getName());

        //Setting adapter for group selection's spinner.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, namesOfGroups);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.groupOfAthlete.setAdapter(adapterSpinner);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillAthleteInformation() {
        this.nameOfAthlete.setText(this.editAthlete.getName());
        this.surnameOfAthlete.setText(this.editAthlete.getSurname());
        this.groupOfAthlete.setSelection(this.editAthlete.getGroupID());
        this.dayOfBirthOfAthlete.setText(String.format("%s: %s", getString(R.string.DAY_OF_BIRTH), this.editAthlete.getDayOfBirth()));
        this.addAthleteBtn.setText(getString(R.string.CHANGE_ATHLETE_INFORMATION));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createAthlete(View view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        String name = this.nameOfAthlete.getText().toString().trim();
        String surname = this.surnameOfAthlete.getText().toString().trim();

        //Checking if the athlete's name and surname are filled in.
        if (this.checkExistenceOfData(this.nameOfAthlete, name, getString(R.string.REQUIRED_NAME_OF_ATHLETE)) ||
                this.checkExistenceOfData(this.surnameOfAthlete, surname, getString(R.string.REQUIRED_SURNAME_OF_ATHLETE)))
            return;

        //Checking if the athlete to be created is already a member of the club.
        if (!this.checkPossibilityToAddAthlete(name, surname)) {
            Toast.makeText(this, getString(R.string.ATHLETE_ALREADY_EXISTS), Toast.LENGTH_LONG).show();
            return;
        }

        //Checking if the athlete's day of birth is filled in.
        String dayOfBirth = this.dayOfBirthOfAthlete.getText().toString();
        if (dayOfBirth.indexOf(':') == -1) {
            this.dayOfBirthOfAthlete.setError(getString(R.string.REQUIRED_DAY_OF_BIRTH_OF_ATHLETE));
            return;
        }
        dayOfBirth = dayOfBirth.substring(dayOfBirth.indexOf(':') + 2);

        //Setting athlete's ID and selected group.
        int athleteID;
        if (this.editAthlete != null)
            athleteID = this.editAthlete.getID();
        else
            athleteID = this.club.getNumberOfAthletes() + 1;
        String groupID = String.valueOf(this.groupOfAthlete.getSelectedItemPosition());

        //Writing athlete's data to the database.
        root.child(getString(R.string.ATHLETES_CHILD_DB) + "/" + athleteID + "/" + getString(R.string.NAME_DB)).setValue(this.nameOfAthlete.getText().toString().trim());
        root.child(getString(R.string.ATHLETES_CHILD_DB) + "/" + athleteID + "/" + getString(R.string.SURNAME_DB)).setValue(this.surnameOfAthlete.getText().toString().trim());
        root.child(getString(R.string.ATHLETES_CHILD_DB) + "/" + athleteID + "/" + getString(R.string.GROUP_ID_DB)).setValue(groupID);
        root.child(getString(R.string.ATHLETES_CHILD_DB) + "/" + athleteID + "/" + getString(R.string.NUMBER_OF_TRAININGS_DB)).setValue(0);
        root.child(getString(R.string.ATHLETES_CHILD_DB) + "/" + athleteID + "/" + getString(R.string.DAY_OF_BIRTH_DB)).setValue(dayOfBirth);
        this.loadAthletesPage();
    }

    private boolean checkExistenceOfData(EditText editText, String data, String value) {
        if (data.equals(getString(R.string.EMPTY_STRING))) {
            editText.setError(value);
            return true;
        }
        return false;
    }

    private boolean checkPossibilityToAddAthlete(String name, String surname) {
        if (editAthlete != null)
            return true;
        for (Member athlete : this.club.getMembersOfClub()) {
            if (athlete.getName().equals(name) && athlete.getSurname().equals(surname))
                return false;
        }
        return true;
    }

    private void loadAthletesPage() {
        //Creating new intent of Home Activity after creating new athlete.
        Intent athletesPage = new Intent(this, HomeActivity.class);
        athletesPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        athletesPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        athletesPage.putExtra(getString(R.string.LOAD_DATA_EXTRA),this. loadData);
        athletesPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        athletesPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        athletesPage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), R.id.athletesFragment);
        startActivity(athletesPage);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"DefaultLocale", "ResourceType"})
    public void displayDateSelection(View view) {
        //Setting listener for date selection.
        DatePickerDialog.OnDateSetListener setListener;
        setListener = (view1, year, month, dayOfMonth) -> this.dayOfBirthOfAthlete.setText(String.format(getString(R.string.DAY_OF_BIRTH) + ": %02d.%02d.%d", dayOfMonth, month + 1, year));

        //Getting current date.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Creating date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
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