package com.example.tritendence.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AttendanceSheetFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String sportSelection, theme;
    private Group group;
    private TrainingUnit unit;
    private String currentTrainersName;
    private LocalDate date;
    private AttendanceData attendanceData;

    //Layout's items
    private ConstraintLayout secondTrainerLayout, thirdTrainerLayout;
    private ListView attendanceSheet;
    private Spinner firstTrainersName, secondTrainersName, thirdTrainersName;
    private AutoCompleteTextView note;
    private TextView trainingData, nameOfGroup;
    private Button confirmation;

    private ArrayList<String> membersOfGroup;
    private ArrayAdapter<String> adapterListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.loadData = (LoadData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.sportSelection = requireActivity().getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.theme = requireActivity().getIntent().getExtras().getString(getString(R.string.THEME_EXTRA));
        this.group = (Group) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.GROUP_EXTRA));
        this.unit = (TrainingUnit) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRAINING_UNIT_EXTRA));
        this.currentTrainersName = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.date = (LocalDate) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.DATE_EXTRA));
        this.attendanceData = (AttendanceData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.ATTENDANCE_DATA_EXTRA));
        this.initializeLayoutItems(view);

        //Filling attendance sheet information.
        this.trainingData.setText(String.format("%s\n%s - %s", this.unit.getSportTranslation(), this.unit.getDay(), this.unit.getTime()));
        this.nameOfGroup.setText(this.group.getName());
        this.addTrainersNames(this.firstTrainersName);
        this.fillAttendedAthletes();

        if (this.attendanceData != null)
            this.fillAttendanceSheetInformation();
    }

    private void initializeLayoutItems(View view) {
        this.note = view.findViewById(R.id.attendanceNote);
        this.trainingData = view.findViewById(R.id.trainingData);
        this.nameOfGroup = view.findViewById(R.id.attendanceGroupName);
        this.firstTrainersName = view.findViewById(R.id.firstTrainersName);
        this.secondTrainersName = view.findViewById(R.id.secondTrainersName);
        this.secondTrainerLayout = view.findViewById(R.id.secondTrainerSelection);
        this.thirdTrainersName = view.findViewById(R.id.thirdTrainersName);
        this.thirdTrainerLayout = view.findViewById(R.id.thirdTrainerSelection);
        this.attendanceSheet = view.findViewById(R.id.attendanceSheet);
        this.confirmation = view.findViewById(R.id.attendanceConfirmationButton);
    }

    public void addTrainersNames(Spinner trainersNameSpinner) {
        //Adding trainer's names to the spinner beginning with the name of currently signed trainer, followed by other trainers.
        ArrayList<String> namesOfTrainers = new ArrayList<>();
        namesOfTrainers.add(this.currentTrainersName);
        namesOfTrainers = this.club.getNamesOfTrainers(namesOfTrainers);

        //Creating adapter and setting it to the trainer's spinner.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, namesOfTrainers);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainersNameSpinner.setAdapter(adapterSpinner);

        //Setting on item selected listener to the spinner to change color of currently selected item.
        trainersNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.parseColor("#E6E6E6"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillAttendedAthletes() {
        //Getting list of attended athletes, creating adapter and setting it to the list of attended athletes.
        this.membersOfGroup = this.group.getNamesOfAthletesOfGroup();
        this.adapterListView = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, this.membersOfGroup);
        this.attendanceSheet.setAdapter(this.adapterListView);

        //Setting on click listener to attendance confirmation button.
        this.confirmation.setOnClickListener(v -> {
            ArrayList<String> athletesNames = new ArrayList<>();

            //Getting names of attended athletes.
            for (int i = 0; i < this.attendanceSheet.getCount(); i++) {
                if (this.attendanceSheet.isItemChecked(i)) {
                    String name = this.attendanceSheet.getItemAtPosition(i).toString();
                    athletesNames.add(name);
                }
            }

            //Finding athletes by their names selected in the list.
            findAthletesByName(athletesNames);
            Toast.makeText(getActivity(), getString(R.string.ATTENDANCE_SUCCESS), Toast.LENGTH_LONG).show();
            loadAttendancePage();
        });
    }

    private void fillAttendanceSheetInformation() {
        //Setting selection of the first spinner to the first trainer of the filled attendance sheet.
        if (this.attendanceData.getNumberOfTrainers() >= 1) {
            for (int i = 0; i < this.firstTrainersName.getChildCount(); i++) {
                if (attendanceData.getTrainer(0).getFullName().equals(this.firstTrainersName.getItemAtPosition(i).toString()))
                    this.firstTrainersName.setSelection(i);
            }
        }

        //Setting selection of the second spinner to the second trainer of the filled attendance sheet.
        if (this.attendanceData.getNumberOfTrainers() >= 2) {
            for (int i = 0; i < this.secondTrainersName.getChildCount(); i++) {
                if (attendanceData.getTrainer(1).getFullName().equals(this.secondTrainersName.getItemAtPosition(i).toString()))
                    this.secondTrainersName.setSelection(i);
            }
        }

        //Setting selection of the third spinner to the third trainer of the filled attendance sheet.
        if (this.attendanceData.getNumberOfTrainers() >= 3) {
            for (int i = 0; i < this.thirdTrainersName.getChildCount(); i++) {
                if (attendanceData.getTrainer(2).getFullName().equals(this.thirdTrainersName.getItemAtPosition(i).toString()))
                    this.thirdTrainersName.setSelection(i);
            }
        }

        //Setting attended athletes of the filled attendance sheet in the list of athletes to true.
        boolean athleteInList;
        for (Athlete attendedAthlete : this.attendanceData.getAttendedAthletes()) {
            athleteInList = false;
            //Looking for attended athlete in the list of athletes of the group.
            for (int i = 0; i < this.attendanceSheet.getChildCount(); i++) {
                if (attendedAthlete.getFullName().equals(this.attendanceSheet.getItemAtPosition(i).toString())) {
                    this.attendanceSheet.setItemChecked(i, true);
                    athleteInList = true;
                }
            }

            //If the attended athlete is not find in the list of athletes of the group it is added to the list and checked true.
            if (!athleteInList) {
                this.membersOfGroup.add(attendedAthlete.getFullName());
                this.adapterListView.notifyDataSetChanged();
                this.attendanceSheet.setItemChecked(this.attendanceSheet.getChildCount() - 1, true);
            }
        }

        //Setting note of the filled attendance sheet.
        this.note.setText(this.attendanceData.getNote());
    }

    private void loadAttendancePage() {
        //Creating intent of Home Activity to return to home page after confirming group's attendance.
        Intent attendancePage = new Intent(getActivity(), HomeActivity.class);
        attendancePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        attendancePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        attendancePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.currentTrainersName);
        attendancePage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        attendancePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), R.id.attendanceFragment);
        attendancePage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        startActivity(attendancePage);
        requireActivity().finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void findAthletesByName(ArrayList<String> athletesNames) {
        ArrayList<Athlete> requestedAthletes = new ArrayList<>();
        //Finding an athlete in the club by its name and increasing number of trainings of the athlete.
        for (Member athlete : this.club.getMembersOfClub()) {
            for (String name : athletesNames) {
                if (name.equals(athlete.getFullName())) {
                    requestedAthletes.add((Athlete) athlete);
                    ((Athlete) athlete).addAttendedTraining();
                }
            }
        }

        //Saving filled attendance.
        saveAttendance(requestedAthletes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveAttendance(ArrayList<Athlete> athletes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        ArrayList<String> trainersNames = new ArrayList<>();

        DecimalFormat dateFormat= new DecimalFormat("00");
        String noteText = this.note.getText().toString().trim();

        //Getting names of the trainers.
        trainersNames.add((String) this.firstTrainersName.getSelectedItem());
        if (this.secondTrainerLayout.getVisibility() == View.VISIBLE)
            trainersNames.add((String) this.secondTrainersName.getSelectedItem());
        if (this.thirdTrainerLayout.getVisibility() == View.VISIBLE)
            trainersNames.add((String) this.thirdTrainersName.getSelectedItem());

        //Creating map of names of attended athletes.
        Map<String, String> attendanceData = new HashMap<>();
        int number = 1;
        for (Athlete athlete : athletes) {
            attendanceData.put(String.valueOf(number), athlete.getFullName());
            number++;
        }
        attendanceData = this.analyzeNote(noteText, attendanceData, number);
        //Creating date format consisting of date of the attendance, time of the training unit and group ID.
        String dateInformation = String.valueOf(this.date.getYear()) + String.valueOf(dateFormat.format(this.date.getMonthValue())) + String.valueOf(dateFormat.format(this.date.getDayOfMonth())) + "_" + this.unit.getTime() + "_" + this.group.getID();

        int numberOfFilledAttendance = this.club.getNumberOfFilledAttendances() + 1;
        //Writing attendance data to the database.
        root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.DATE_DB)).setValue(dateInformation);
        root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.SPORT_DB)).setValue(this.unit.getSportTranslation());
        if (attendanceData.size() == 0)
            root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.ATHLETES_DB)).setValue(getString(R.string.EMPTY_STRING));
        else
            root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.ATHLETES_DB)).setValue(attendanceData);
        root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.NOTE_DB)).setValue(noteText);
        for (int i = 0; i < trainersNames.size(); i++) {
            String trainerID = getString(R.string.TRAINER_DB) + String.valueOf(i+1);
            root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + trainerID).setValue(trainersNames.get(i));
        }

        //If the attendance data are the first ones then delete the sign of the empty attendance's child.
        if (this.club.getNumberOfFilledAttendances() == 0)
            root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + getString(R.string.FIRST_CHILD_DB)).removeValue();

        this.addTrainingToTrainers(trainersNames);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Map<String, String> analyzeNote(String note, Map<String, String> attendanceData, int number) {
        ArrayList<String> wordsOfNote = new ArrayList<>(Arrays.asList(note.split(getString(R.string.SPACE))));
        for (int i = 1; i < wordsOfNote.size(); i++) {
            String word = wordsOfNote.get(i);
            for (Member athlete : this.club.getAthletesSortedByAlphabet()) {
                if (athlete.getSurname().equals(word) && athlete.getName().equals(wordsOfNote.get(i-1))) {
                    attendanceData.put(String.valueOf(number), athlete.getFullName());
                    number++;
                }
            }
        }

        return attendanceData;
    }

    private void addTrainingToTrainers(ArrayList<String> trainersNames) {
        //Finding trainer by its name and increasing number of training units of the trainer.
        for (String nameOfTrainer : trainersNames) {
            for (Member trainer : this.club.getTrainersSortedByAlphabet()) {
                if (trainer.getFullName().equals(nameOfTrainer))
                    ((Trainer) trainer).addTraining();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
    }
}