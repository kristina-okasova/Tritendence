package com.example.tritendence.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.users.Athlete;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AttendanceSheetFragment extends Fragment {
    private final static String ATHLETES_CHILD_DATABASE = "Athletes";
    private final static String ATHLETES_NAME = "name";
    private final static String ATHLETES_SURNAME = "surname";
    private final static String ATHLETES_EMAIL = "email";
    private final static String ATHLETES_GROUP_ID = "groupID";
    private final static String ATTENDANCE_CHILD_DATABASE = "Attendance";

    private ListView attendanceSheet;
    private DatabaseReference database;
    private Button confirmation;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> membersOfGroup;
    private TextView nameOfGroup;

    public AttendanceSheetFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initializeMembersOfGroup(view);

        this.nameOfGroup = view.findViewById(R.id.nameOfGroup);
        String groupName = getActivity().getIntent().getExtras().getString("GROUP_NAME");
        String time = getActivity().getIntent().getExtras().getString("TRAINING_TIME");
        String sport = getActivity().getIntent().getExtras().getString("SPORT_TYPE");
        this.nameOfGroup.setText(String.format("%s\n%s - %s", groupName, sport, time));

        this.confirmation = view.findViewById(R.id.attendanceConfirmationButton);
        this.confirmation.setOnClickListener(v -> {
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

    private void findAthletesByName(ArrayList<String> names) {
        this.database = FirebaseDatabase.getInstance().getReference().child(ATHLETES_CHILD_DATABASE);

        this.database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Athlete> requestedAthletes = new ArrayList<>();
                int numberOfAthletes = (int) snapshot.getChildrenCount();

                for (int i = 1; i <= numberOfAthletes; i++) {
                    String athleteID = String.valueOf(i);
                    Athlete athlete = new Athlete();
                    athlete.setID(i);
                    athlete.setName(snapshot.child(athleteID).child(ATHLETES_NAME).getValue().toString());
                    athlete.setSurname(snapshot.child(athleteID).child(ATHLETES_SURNAME).getValue().toString());
                    athlete.setEmail(snapshot.child(athleteID).child(ATHLETES_EMAIL).getValue().toString());
                    String athleteGroupID = snapshot.child(athleteID).child(ATHLETES_GROUP_ID).getValue().toString();
                    athlete.setGroupID(Integer.parseInt(athleteGroupID));

                    for (String name : names) {
                        if (name.equals(athlete.getFullName()))
                            requestedAthletes.add(athlete);
                    }
                    saveAttendance(requestedAthletes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void saveAttendance(ArrayList<Athlete> athletes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DecimalFormat dateFormat= new DecimalFormat("00");
        String date = String.valueOf(year) + String.valueOf(dateFormat.format(Double.valueOf(month))) + String.valueOf(dateFormat.format(Double.valueOf(day)));
        String trainingTime = getActivity().getIntent().getExtras().getString("TRAINING_TIME");
        String groupID = getActivity().getIntent().getExtras().getString("GROUP_ID");
        date += "_" + trainingTime + "_" + groupID;

        Map<String, String> attendanceData = new HashMap<>();
        int number = 1;
        for (Athlete athlete : athletes) {
            attendanceData.put(String.valueOf(number), athlete.getFullName());
            number++;
        }

        String groupsName = getActivity().getIntent().getExtras().getString("GROUPS_NAME");
        String trainersName = getActivity().getIntent().getExtras().getString("TRAINERS_NAME");
        String sportType = getActivity().getIntent().getExtras().getString("SPORT_TYPE");

        root.child(ATTENDANCE_CHILD_DATABASE + "/" + date).setValue(attendanceData);
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + date + "/GroupsName").setValue(groupsName);
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + date + "/Sport").setValue(sportType);
        root.child(ATTENDANCE_CHILD_DATABASE + "/" + date + "/Trainer").setValue(trainersName);
    }

    private void initializeMembersOfGroup(View view) {
        membersOfGroup = new ArrayList<>();
        this.database = FirebaseDatabase.getInstance().getReference().child(ATHLETES_CHILD_DATABASE);

        this.database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfAthletes = (int) snapshot.getChildrenCount();

                for (int i = 1; i <= numberOfAthletes; i++) {
                    String athleteID = String.valueOf(i);
                    Athlete athlete = new Athlete();
                    athlete.setID(i);
                    athlete.setName(snapshot.child(athleteID).child(ATHLETES_NAME).getValue().toString());
                    athlete.setSurname(snapshot.child(athleteID).child(ATHLETES_SURNAME).getValue().toString());
                    athlete.setEmail(snapshot.child(athleteID).child(ATHLETES_EMAIL).getValue().toString());
                    String athleteGroupID = snapshot.child(athleteID).child(ATHLETES_GROUP_ID).getValue().toString();
                    athlete.setGroupID(Integer.parseInt(athleteGroupID));

                    String groupID = getActivity().getIntent().getExtras().getString("GROUP_ID");

                    if (athlete.getGroupID() == Integer.parseInt(groupID))
                        membersOfGroup.add(athlete.getFullName());
                }

                notifyAttendanceSheet(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void notifyAttendanceSheet(View view) {
        this.attendanceSheet = view.findViewById(R.id.attendanceSheet);
        this.adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, this.membersOfGroup);
        this.attendanceSheet.setAdapter(this.adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
    }
}