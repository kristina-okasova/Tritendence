package com.example.tritendence.fragments;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.lists.ListScrollable;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditAttendanceSheetFragment extends Fragment {
    private TriathlonClub club;
    private AttendanceData selectedAttendanceData;
    private TextView trainingData, groupName;
    private ConstraintLayout secondTrainerLayout, thirdTrainerLayout;
    private Spinner firstTrainersName, secondTrainersName, thirdTrainersName;
    private ListScrollable attendanceSheet;
    private AutoCompleteTextView note;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.selectedAttendanceData = (AttendanceData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.ATTENDANCE_DATA_EXTRA));

        this.trainingData = view.findViewById(R.id.trainingDataEdit);
        this.groupName = view.findViewById(R.id.attendanceGroupNameEdit);
        this.firstTrainersName = view.findViewById(R.id.firstTrainersNameEdit);
        this.secondTrainersName = view.findViewById(R.id.secondTrainersNameEdit);
        this.secondTrainerLayout = view.findViewById(R.id.secondTrainerSelectionEdit);
        this.thirdTrainersName = view.findViewById(R.id.thirdTrainersNameEdit);
        this.thirdTrainerLayout = view.findViewById(R.id.thirdTrainerSelectionEdit);
        this.attendanceSheet = view.findViewById(R.id.attendanceSheetEdit);
        this.note = view.findViewById(R.id.attendanceNoteEdit);

        this.addTrainersNames(this.firstTrainersName, this.selectedAttendanceData.getTrainer(0).getFullName());
        this.fillAttendanceInformation();

        Button confirmation = view.findViewById(R.id.attendanceEditButton);
        confirmation.setOnClickListener(v -> {
            ArrayList<String> athletesNames = new ArrayList<>();

            for (int i = 0; i < this.attendanceSheet.getCount(); i++) {
                if (this.attendanceSheet.isItemChecked(i)) {
                    String name = this.attendanceSheet.getItemAtPosition(i).toString();
                    athletesNames.add(name);
                }
            }

            findAthletesByName(athletesNames);
            Toast.makeText(getActivity(), athletesNames.toString(), Toast.LENGTH_LONG).show();
        });
    }

    public void addTrainersNames(Spinner trainersNameSpinner, String currentTrainersName) {
        ArrayList<String> namesOfTrainers = new ArrayList<>();
        namesOfTrainers.add(currentTrainersName);
        namesOfTrainers = this.club.getNamesOfTrainers(namesOfTrainers);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, namesOfTrainers);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainersNameSpinner.setAdapter(adapterSpinner);

        trainersNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.parseColor("#DEF2F1"));
                ((TextView) view).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void findAthletesByName(ArrayList<String> athletesNames) {
        ArrayList<Athlete> requestedAthletes = new ArrayList<>();
        for (Member athlete : this.club.getMembersOfClub()) {
            for (String name : athletesNames) {
                if (name.equals(athlete.getFullName()))
                    requestedAthletes.add((Athlete) athlete);
            }
        }

        saveEditAttendance(requestedAthletes, this.club.getNumberOfFilledAttendances());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillAttendanceInformation() {
        this.trainingData.setText(String.format("%s\n%s - %s", this.selectedAttendanceData.getSport(), this.selectedAttendanceData.getDayTranslation(), this.selectedAttendanceData.getTime()));
        this.groupName.setText(this.selectedAttendanceData.getGroup().getName());
        for (int i = 0; i < this.firstTrainersName.getChildCount(); i++) {
            if (((TextView) this.firstTrainersName.getChildAt(i)).getText().toString().equals(this.selectedAttendanceData.getTrainer(0).getFullName()))
                this.firstTrainersName.setSelection(i);
        }

        if (this.selectedAttendanceData.getNumberOfTrainers() == 2) {
            this.addTrainersNames(this.secondTrainersName, this.selectedAttendanceData.getTrainer(1).getFullName());
            this.secondTrainerLayout.setVisibility(View.VISIBLE);
        }

        if (this.selectedAttendanceData.getNumberOfTrainers() == 3) {
            this.addTrainersNames(this.thirdTrainersName, this.selectedAttendanceData.getTrainer(2).getFullName());
            this.thirdTrainerLayout.setVisibility(View.VISIBLE);
        }

        ArrayList<String> membersOfGroup = this.selectedAttendanceData.getGroup().getNamesOfAthletesOfGroup();
        ArrayAdapter<String> adapterListView = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, membersOfGroup);
        this.attendanceSheet.setAdapter(adapterListView);

        for (int i = 0; i < membersOfGroup.size(); i++) {
            String nameOfMember = membersOfGroup.get(i);
            for (Athlete athlete : this.selectedAttendanceData.getAttendedAthletes()) {
                if (athlete.getFullName().equals(nameOfMember))
                    this.attendanceSheet.setItemChecked(i, true);
            }
        }

        this.note.setText(this.selectedAttendanceData.getNote());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEditAttendance(ArrayList<Athlete> athletes, int numberOfFilledAttendance) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        ArrayList<String> trainersNames = new ArrayList<>();

        DecimalFormat dateFormat= new DecimalFormat("00");
        String noteText = this.note.getText().toString().trim();
        trainersNames.add((String) this.firstTrainersName.getSelectedItem());

        if (this.secondTrainerLayout.getVisibility() == View.VISIBLE)
            trainersNames.add((String) this.secondTrainersName.getSelectedItem());
        if (this.thirdTrainerLayout.getVisibility() == View.VISIBLE)
            trainersNames.add((String) this.thirdTrainersName.getSelectedItem());

        Map<String, String> attendanceData = new HashMap<>();
        int number = 1;
        for (Athlete athlete : athletes) {
            attendanceData.put(String.valueOf(number), athlete.getFullName());
            number++;
        }
        String dateInformation = String.valueOf(this.selectedAttendanceData.getLocalDate().getYear()) + String.valueOf(dateFormat.format(this.selectedAttendanceData.getLocalDate().getMonthValue())) + String.valueOf(dateFormat.format(this.selectedAttendanceData.getLocalDate().getDayOfMonth())) + "_" + this.selectedAttendanceData.getTime() + "_" + this.selectedAttendanceData.getGroup().getID();

        numberOfFilledAttendance++;
        root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.DATE_DB)).setValue(dateInformation);
        root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.SPORT_DB)).setValue(this.selectedAttendanceData.getSport());
        if (attendanceData.size() == 0)
            root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.ATHLETES_DB)).setValue(getString(R.string.EMPTY_STRING));
        else
            root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.ATHLETES_DB)).setValue(attendanceData);
        root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + getString(R.string.NOTE_DB)).setValue(noteText);
        for (int i = 0; i < trainersNames.size(); i++) {
            String trainerID = getString(R.string.TRAINER_DB) + String.valueOf(i+1);
            root.child(getString(R.string.ATTENDANCE_CHILD_DB) + "/" + numberOfFilledAttendance + "/" + trainerID).setValue(trainersNames.get(i));
        }

        this.addTrainingToTrainers(trainersNames);
    }

    private void addTrainingToTrainers(ArrayList<String> trainersNames) {
        for (String nameOfTrainer : trainersNames) {
            for (Member trainer : this.club.getTrainersSortedByAlphabet()) {
                if (trainer.getFullName().equals(nameOfTrainer))
                    ((Trainer) trainer).addTraining();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_attendance_sheet, container, false);
    }
}