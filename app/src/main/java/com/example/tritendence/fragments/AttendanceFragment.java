package com.example.tritendence.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AdapterOfExpendableList;
import com.example.tritendence.model.AddEntity;
import com.example.tritendence.model.TrainingUnit;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceFragment extends Fragment {
    private final static String GROUPS_CHILD_DATABASE = "Groups";
    private final static String GROUPS_NAME = "Name";
    private final static String GROUPS_TIMETABLE = "Timetable";

    private HomeActivity activity;
    private List<String> daysOfTheWeek;
    private List<String> groupNames;
    private Map<String, List<String>> timetable;
    private ExpandableListView expandableTimetable;
    private ExpandableListAdapter adapter;
    private DatabaseReference database;

    private final HashMap<Integer, String> sports = new HashMap<Integer, String>() {{
        put(1, "Athletics");
        put(2, "Swimming");
        put(3, "Cycling");
    }};

    public AttendanceFragment() {
        this.timetable = new HashMap<>();
    }

    public AttendanceFragment(HomeActivity activity) {
        this.timetable = new HashMap<>();
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initializeTimetable();
        this.addGroups();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.expandableTimetable = view.findViewById(R.id.timetable);
        this.expandableTimetable.setAdapter(new AdapterOfExpendableList(this.activity, new HomeActivity(), this, this.daysOfTheWeek, this.timetable));
        this.expandableTimetable.setGroupIndicator(null);

        this.expandableTimetable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (previousGroup != -1 && groupPosition != previousGroup)
                    expandableTimetable.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    private void addGroups() {
        this.database = FirebaseDatabase.getInstance().getReference().child(GROUPS_CHILD_DATABASE);

        this.database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfGroups = (int) snapshot.getChildrenCount();

                for (int i = 1; i <= numberOfGroups; i++) {
                    String groupID = String.valueOf(i);
                    String nameOfGroup = snapshot.child(groupID).child(GROUPS_NAME).getValue().toString();

                    for (Map.Entry<Integer, String> sport : sports.entrySet()) {
                        String sportName = sport.getValue();

                        int numberOfTrainingDays = (int) snapshot.child(groupID).child(GROUPS_TIMETABLE).child(sportName).getChildrenCount();
                        for (int k = 1; k <= numberOfTrainingDays; k++) {
                            String trainingUnitID = String.valueOf(k);
                            TrainingUnit unit = snapshot.child(groupID).child(GROUPS_TIMETABLE).child(sportName).child(trainingUnitID).getValue(TrainingUnit.class);

                            addGroupToParticularDay(nameOfGroup, unit.getDay(), unit.getTime(), sportName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addGroupToParticularDay(String name, String day, String time, String sport) {
        switch (sport) {
            case "Athletics":
                sport = "Atletika";
                break;
            case "Swimming":
                sport = "Plávanie";
                break;
            case "Cycling":
                sport = "Cyklistika";
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            this.timetable.computeIfAbsent(day, k -> new ArrayList<>()).add(time + " " + sport + " " + name);
    }

    private void initializeTimetable() {
        this.daysOfTheWeek = new ArrayList<>();
        this.daysOfTheWeek.add(getString(R.string.MONDAY));
        this.daysOfTheWeek.add(getString(R.string.TUESDAY));
        this.daysOfTheWeek.add(getString(R.string.WEDNESDAY));
        this.daysOfTheWeek.add(getString(R.string.THURSDAY));
        this.daysOfTheWeek.add(getString(R.string.FRIDAY));
        this.daysOfTheWeek.add(getString(R.string.SATURDAY));
        this.daysOfTheWeek.add(getString(R.string.SUNDAY));

        this.timetable.put(getString(R.string.MONDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.TUESDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.WEDNESDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.THURSDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.FRIDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.SATURDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.SUNDAY), new ArrayList<>());
    }
}
