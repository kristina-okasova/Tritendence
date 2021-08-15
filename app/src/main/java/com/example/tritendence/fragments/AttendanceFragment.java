package com.example.tritendence.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AdapterOfExpendableList;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceFragment extends Fragment {
    private HomeActivity activity;
    private List<String> daysOfTheWeek;
    private final Map<String, List<String>> timetable;
    private ExpandableListView expandableTimetable;
    private TriathlonClub club;

    public AttendanceFragment() {
        this.timetable = new HashMap<>();
    }

    public AttendanceFragment(HomeActivity activity) {
        this.timetable = new HashMap<>();
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.initializeTimetable();
        this.addGroups();

        this.expandableTimetable = view.findViewById(R.id.timetable);
        this.expandableTimetable.setAdapter(new AdapterOfExpendableList(this.activity, this.daysOfTheWeek, this.timetable, club));
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

    public void addGroups() {
        for (Group group : this.club.getGroupsOfClub()) {
            for (TrainingUnit unit : group.getTimetable())
                addGroupToParticularDay(group.getName(), unit.getDay(), unit.getTime(), unit.getSport());
        }
    }

    private void addGroupToParticularDay(String name, String day, String time, String sport) {
        switch (sport) {
            case "Athletics":
                sport = getString(R.string.ATHLETICS);
                break;
            case "Swimming":
                sport = getString(R.string.SWIMMING);
                break;
            case "Cycling":
                sport = getString(R.string.CYCLING);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sport);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            this.timetable.computeIfAbsent(day, k -> new ArrayList<>()).add(time + " " + sport + " " + name);
    }

    public void initializeTimetable() {
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
