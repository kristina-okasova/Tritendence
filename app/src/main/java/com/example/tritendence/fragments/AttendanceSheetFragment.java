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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceSheetFragment extends Fragment {
    private final static String ATTENDANCE_CHILD_DATABASE = "Attendance";

    private ListView attendanceSheet;
    private TriathlonClub club;
    private Group group;
    private TrainingUnit unit;
    private Spinner firstTrainersName, secondTrainersName, thirdTrainersName;
    private ConstraintLayout secondTrainerLayout, thirdTrainerLayout;
    private String currentTrainersName;
    private AutoCompleteTextView note;
    private LocalDate date;

    public AttendanceSheetFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.group = (Group) requireActivity().getIntent().getExtras().getSerializable("GROUP");
        this.unit = (TrainingUnit) requireActivity().getIntent().getExtras().getSerializable("TRAINING_UNIT");
        this.currentTrainersName = requireActivity().getIntent().getExtras().getString("SIGNED_USER");
        this.date = (LocalDate) requireActivity().getIntent().getExtras().getSerializable("DATE");
        this.note = view.findViewById(R.id.attendanceNote);
        ArrayList<String> membersOfGroup = this.group.getNamesOfAthletesOfGroup();

        TextView trainingData = view.findViewById(R.id.trainingData);
        trainingData.setText(String.format("%s\n%s - %s", this.unit.getSportTranslation(), this.unit.getDay(), this.unit.getTime()));

        TextView nameOfGroup = view.findViewById(R.id.attendanceGroupName);
        nameOfGroup.setText(this.group.getName());

        this.firstTrainersName = view.findViewById(R.id.firstTrainersName);
        this.secondTrainersName = view.findViewById(R.id.secondTrainersName);
        this.secondTrainerLayout = view.findViewById(R.id.secondTrainerSelection);
        this.thirdTrainersName = view.findViewById(R.id.thirdTrainersName);
        this.thirdTrainerLayout = view.findViewById(R.id.thirdTrainerSelection);
        this.addTrainersNames(this.firstTrainersName);

        this.attendanceSheet = view.findViewById(R.id.attendanceSheet);
        ArrayAdapter<String> adapterListView = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, membersOfGroup);
        this.attendanceSheet.setAdapter(adapterListView);

        Button confirmation = view.findViewById(R.id.attendanceConfirmationButton);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void findAthletesByName(ArrayList<String> athletesNames) {
        ArrayList<Athlete> requestedAthletes = new ArrayList<>();
        for (Member athlete : this.club.getMembersOfClub()) {
            for (String name : athletesNames) {
                if (name.equals(athlete.getFullName()))
                    requestedAthletes.add((Athlete) athlete);
            }
        }

        saveAttendance(requestedAthletes, this.club.getNumberOfFilledAttendances());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveAttendance(ArrayList<Athlete> athletes, int numberOfFilledAttendance) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        ArrayList<String> trainersNames = new ArrayList<>();

        DecimalFormat dateFormat= new DecimalFormat("00");
        String noteText = this.note.getText().toString();
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
        String dateInformation = String.valueOf(this.date.getYear()) + String.valueOf(dateFormat.format(this.date.getMonthValue())) + String.valueOf(dateFormat.format(this.date.getDayOfMonth())) + "_" + this.unit.getTime() + "_" + this.group.getID();

        numberOfFilledAttendance++;
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/Date").setValue(dateInformation);
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/Sport").setValue(this.unit.getSportTranslation());
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/Athletes").setValue(attendanceData);
        if (noteText.length() != 0)
            root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/Note").setValue(noteText);
        for (int i = 0; i < trainersNames.size(); i++) {
            String trainerID = "Trainer" + String.valueOf(i+1);
            root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/" + trainerID).setValue(trainersNames.get(i));
        }
    }

    public void addTrainersNames(Spinner trainersNameSpinner) {
        ArrayList<String> namesOfTrainers = new ArrayList<>();
        namesOfTrainers.add(this.currentTrainersName);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
    }
}