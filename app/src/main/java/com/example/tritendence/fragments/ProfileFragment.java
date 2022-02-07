package com.example.tritendence.fragments;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Admin;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

public class ProfileFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private String signedTrainer, theme;

    //Layout's items
    private ListView trainersTrainings;
    private TextView signedTrainerName, signedTrainerEmail, numberOfTrainings, firstWeekOfTheYearText;
    private Spinner themeOfUser, firstWeekOfTheYear;

    private final HomeActivity activity;

    public ProfileFragment(HomeActivity activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedTrainer = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.theme = requireActivity().getIntent().getExtras().getString(getString(R.string.THEME_EXTRA));

        this.initializeLayoutItems(view);
        this.setThemesSpinner();
        this.setWeekSpinner();

        //Fill information about currently signed trainer.
        this.fillTrainerInformation();
        this.displayFirstWeekSelection();

        //Setting listener of themes spinner.
        this.themeOfUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (themeOfUser.getSelectedItemId() == 0)
                    return;
                String selectedTheme = themeOfUser.getSelectedItem().toString();
                Member currentTrainer = null;
                //Finding currently signed user by its name.
                for (Member trainer : club.getTrainersSortedByAlphabet()) {
                    if (trainer instanceof Trainer && trainer.getFullName().equals(signedTrainer))
                        currentTrainer = trainer;
                }
                if (club.getAdminOfClub().getFullName().equals(signedTrainer))
                    currentTrainer = club.getAdminOfClub();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                //Setting new theme to the signed user.
                if (currentTrainer instanceof Trainer) {
                    if (((Trainer) currentTrainer).getTheme().equals(selectedTheme))
                        return;

                    database.child(getString(R.string.TRAINERS_DB) + "/" + ((Trainer) currentTrainer).getIDText() + "/" + (getString(R.string.THEME_DB))).setValue(selectedTheme);
                    Objects.requireNonNull((Trainer) currentTrainer).setTheme(selectedTheme);
                }

                if (currentTrainer instanceof Admin) {
                    if (((Admin) currentTrainer).getTheme().equals(selectedTheme))
                        return;

                    database.child(getString(R.string.ADMIN_DB) + "/" + (getString(R.string.THEME_DB))).setValue(selectedTheme);
                    Objects.requireNonNull((Admin) currentTrainer).setTheme(selectedTheme);
                }

                activity.changeTheme(selectedTheme);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initializeLayoutItems(View view) {
        this.signedTrainerName = view.findViewById(R.id.signedTrainerName);
        this.signedTrainerEmail = view.findViewById(R.id.signedTrainerEmail);
        this.numberOfTrainings = view.findViewById(R.id.numberOfTrainingUnits);
        this.trainersTrainings = view.findViewById(R.id.trainersTrainings);
        this.themeOfUser = view.findViewById(R.id.themeOfUser);
        this.firstWeekOfTheYearText = view.findViewById(R.id.textView8);
        this.firstWeekOfTheYear = view.findViewById(R.id.firstWeekOfTheYear);
    }

    private void setThemesSpinner() {
        ArrayList<String> listOfThemes = new ArrayList<>();
        switch (this.theme) {
            case "DarkRed":
                listOfThemes.add("DarkRed");
                listOfThemes.add("DarkBlue");
                listOfThemes.add("LightRed");
                listOfThemes.add("LightBlue");
                break;
            case "DarkBlue":
                listOfThemes.add("DarkBlue");
                listOfThemes.add("DarkRed");
                listOfThemes.add("LightRed");
                listOfThemes.add("LightBlue");
                break;
            case "LightRed":
                listOfThemes.add("LightRed");
                listOfThemes.add("DarkBlue");
                listOfThemes.add("DarkRed");
                listOfThemes.add("LightBlue");
                break;
            case "LightBlue":
                listOfThemes.add("LightBlue");
                listOfThemes.add("DarkBlue");
                listOfThemes.add("LightRed");
                listOfThemes.add("DarkRed");
                break;
        }

        //Setting adapter for themes available in app.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, listOfThemes);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.themeOfUser.setAdapter(adapterSpinner);

        //Setting default value of theme to the selected theme of currently signed user.
        this.themeOfUser.setSelection(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekSpinner() {
        LocalDate mondayOfTheWeek = this.getFirstMonday(Calendar.getInstance().get(Calendar.YEAR));
        ArrayList<String> numberOfWeeks = new ArrayList<>();
        for (int i = 1; i <= Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_YEAR); i++) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");
            numberOfWeeks.add(String.valueOf(i) + ". " + getString(R.string.WEEK_LOW) + ": " + formatter.format(mondayOfTheWeek)
                    + " - " + formatter.format((mondayOfTheWeek.plusDays(6))));
            mondayOfTheWeek = mondayOfTheWeek.plusDays(7);
        }

        //Setting adapter for the first week of the current year.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, numberOfWeeks);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.firstWeekOfTheYear.setAdapter(adapterSpinner);

        //Setting default value of the first week to the currently selected week.
        this.firstWeekOfTheYear.setSelection(this.club.getFirstWeek() - 1);

        this.firstWeekOfTheYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == club.getFirstWeek() - 1)
                    return;
                AlertDialog.Builder acknowledgement = new AlertDialog.Builder(getContext());
                acknowledgement.setCancelable(true);
                acknowledgement.setTitle(getString(R.string.ACKNOWLEDGMENT));
                acknowledgement.setMessage(getString(R.string.CHANGE_OF_FIRST_WEEK));
                acknowledgement.setPositiveButton(getString(R.string.YES), (dialog, which) -> {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference root = database.getReference();

                        club.setFirstWeek(position + 1);
                        root.child(getString(R.string.FIRST_WEEK_DB)).setValue(position + 1);
                    });
                acknowledgement.setNegativeButton(getString(R.string.NO), (dialog, which) -> {});

                AlertDialog dialog = acknowledgement.create();
                dialog.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate getFirstMonday(int year) {
        LocalDate now = LocalDate.of(year, Month.JANUARY, 1);
        return now.with(firstInMonth(DayOfWeek.MONDAY));
    }

    private void fillTrainerInformation() {
        ArrayList<HashMap<String, Object>> dataForListOfTrainings = new ArrayList<>();

        //Creating hashmap consisting of the attendance data of signed trainer for adapter.
        for (AttendanceData attendance : this.club.getAttendanceData()) {
            HashMap<String, Object> mappedData = new HashMap<>();

            //Looking for signed trainer in list of trainers of concrete attendance data.
            for (Member trainer : attendance.getTrainers()) {
                if (trainer.getFullName().equals(signedTrainer)) {
                    mappedData.put(getString(R.string.TRAINING_OF_TRAINER_ADAPTER), attendance.getDate() + " " + attendance.getTime() + "\n" + attendance.getGroup().getName());
                    dataForListOfTrainings.add(mappedData);
                }
            }
        }

        //Looking for the signed user by his name.
        for (Member member : this.club.getMembersOfClub()) {
            if ((member instanceof Trainer || member instanceof Admin) && member.getFullName().equals(this.signedTrainer)) {
                this.signedTrainerName.setText(member.getFullName());

                //Fill in data if the signed user is trainer.
                if (member instanceof Trainer) {
                    this.signedTrainerEmail.setText(((Trainer) member).getEmail());
                    this.numberOfTrainings.setText(String.valueOf(dataForListOfTrainings.size()));
                }

                //Fill in data if the signed user is admin.
                if (member instanceof Admin) {
                    this.signedTrainerEmail.setText(((Admin) member).getEmail());
                    this.numberOfTrainings.setText(String.valueOf(dataForListOfTrainings.size()));
                }

                //Linking keys of hashmap to items of the layout used by adapter.
                String[] insertingData = {getString(R.string.TRAINING_OF_TRAINER_ADAPTER)};
                int[] UIData = {R.id.trainersTrainingData};

                //Creating adapter and setting it to the list of attendances.
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfTrainings, R.layout.training_in_list_of_trainers_trainings, insertingData, UIData);
                this.trainersTrainings.setAdapter(adapter);
            }
        }
    }

    private void displayFirstWeekSelection() {
        if (this.signedTrainer.equals(this.club.getAdminOfClub().getFullName())) {
            this.firstWeekOfTheYearText.setVisibility(View.VISIBLE);
            this.firstWeekOfTheYear.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.fillTrainerInformation();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
