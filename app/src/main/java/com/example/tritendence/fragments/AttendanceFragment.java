package com.example.tritendence.fragments;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceFragment extends Fragment {
    private HomeActivity activity;
    private List<String> daysOfTheWeek;
    private List<String> groupNames;
    private Map<String, List<String>> timetable;
    private ExpandableListView expandableTimetable;
    private ExpandableListAdapter adapter;

    public AttendanceFragment() {}

    public AttendanceFragment(HomeActivity activity) {
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
        String[] mondayGroups = {"14:00 Plávanie", "15:00 Plávanie", "15:00 Atletika", "19:00 Plávanie"};
        String[] tuesdayGroups = {"15:00 Atletika", "16:00 Plávanie", "16:00 Atletika", "17:00 Atletika"};
        String[] wednesdayGroups = {"14:00 Plávanie", "15:00 Plávanie", "19:00 Plávanie", "19:00 Atletika"};
        String[] thursdayGroups = {"13:00 Atletika", "13:00 Plávanie", "15:00 Atletika", "15:00 Plávanie"};
        String[] fridayGroups = {"14:00 PLávanie", "14:00 Atletika", "15:00 Atletika", "16:00 Plávanie"};

        this.timetable = new HashMap<>();
        for (String day : this.daysOfTheWeek) {
            if (day.equals(getString(R.string.MONDAY)))
                this.addGroupsToParticularDay(mondayGroups);
            else if (day.equals(getString(R.string.TUESDAY)))
                this.addGroupsToParticularDay(tuesdayGroups);
            else if (day.equals(getString(R.string.WEDNESDAY)))
                this.addGroupsToParticularDay(wednesdayGroups);
            else if (day.equals(getString(R.string.THURSDAY)))
                this.addGroupsToParticularDay(thursdayGroups);
            else if (day.equals(getString(R.string.FRIDAY)))
                this.addGroupsToParticularDay(fridayGroups);

            this.timetable.put(day, this.groupNames);
        }
    }

    private void addGroupsToParticularDay(String[] groupsOfParticularDay) {
        this.groupNames = new ArrayList<>();
        Collections.addAll(this.groupNames, groupsOfParticularDay);
    }

    private void initializeTimetable() {
        this.daysOfTheWeek = new ArrayList<>();
        this.daysOfTheWeek.add(getString(R.string.MONDAY));
        this.daysOfTheWeek.add(getString(R.string.TUESDAY));
        this.daysOfTheWeek.add(getString(R.string.WEDNESDAY));
        this.daysOfTheWeek.add(getString(R.string.THURSDAY));
        this.daysOfTheWeek.add(getString(R.string.FRIDAY));
    }
}
