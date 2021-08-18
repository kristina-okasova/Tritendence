package com.example.tritendence.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AttendanceSheetFragment extends Fragment {
    private final static String ATTENDANCE_CHILD_DATABASE = "Attendance";

    private ListView attendanceSheet;
    private TriathlonClub club;
    private Group group;
    private TrainingUnit unit;
    private Spinner trainersName;
    private String currentTrainersName;

    public AttendanceSheetFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.group = (Group) requireActivity().getIntent().getExtras().getSerializable("GROUP");
        this.unit = (TrainingUnit) requireActivity().getIntent().getExtras().getSerializable("TRAINING_UNIT");
        this.currentTrainersName = requireActivity().getIntent().getExtras().getString("SIGNED_USER");
        ArrayList<String> membersOfGroup = this.group.getNamesOfAthletesOfGroup();

        TextView trainingData = view.findViewById(R.id.trainingData);
        trainingData.setText(String.format("%s\n%s - %s", this.unit.getSportTranslation(), this.unit.getDay(), this.unit.getTime()));

        TextView nameOfGroup = view.findViewById(R.id.attendanceGroupName);
        nameOfGroup.setText(this.group.getName());

        this.trainersName = view.findViewById(R.id.attendanceTrainersName);
        ArrayList<String> namesOfTrainers = new ArrayList<>();
        namesOfTrainers.add(this.currentTrainersName);
        namesOfTrainers = this.club.getNamesOfTrainers(namesOfTrainers);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, namesOfTrainers);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.trainersName.setAdapter(adapterSpinner);

        this.trainersName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(Color.parseColor("#DEF2F1"));
                ((TextView) view).setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.attendanceSheet = view.findViewById(R.id.attendanceSheet);
        ArrayAdapter<String> adapterListView = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, membersOfGroup);
        this.attendanceSheet.setAdapter(adapterListView);

        Button confirmation = view.findViewById(R.id.attendanceConfirmationButton);
        confirmation.setOnClickListener(v -> {
            ArrayList<String> athletesNames = new ArrayList<>();

            for (int i = 0; i < attendanceSheet.getCount(); i++) {
                if (attendanceSheet.isItemChecked(i)) {
                    String name = attendanceSheet.getItemAtPosition(i).toString();
                    athletesNames.add(name);
                }
            }

            findAthletesByName(athletesNames);
            Toast.makeText(getActivity(), athletesNames.toString(), Toast.LENGTH_LONG).show();
        });
    }

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

    private void saveAttendance(ArrayList<Athlete> athletes, int numberOfFilledAttendance) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DecimalFormat dateFormat= new DecimalFormat("00");
        String date = String.valueOf(year) + String.valueOf(dateFormat.format(Double.valueOf(month))) + String.valueOf(dateFormat.format(Double.valueOf(day)));
        date += "_" + this.unit.getTime() + "_" + this.group.getID();

        Map<String, String> attendanceData = new HashMap<>();
        int number = 1;
        for (Athlete athlete : athletes) {
            attendanceData.put(String.valueOf(number), athlete.getFullName());
            number++;
        }

        numberOfFilledAttendance++;
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/" + "/Date").setValue(date);
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/" + "/Sport").setValue(this.unit.getSport());
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/" + "/Trainer").setValue(trainersName);
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/" + "/Athletes").setValue(attendanceData);
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + numberOfFilledAttendance + "/" + "/GroupsName").setValue(this.group.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
    }
}