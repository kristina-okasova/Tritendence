package com.example.tritendence.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.activities.GroupInformationActivity;
import com.example.tritendence.model.AdapterOfExpendableAttendance;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupInformationFragment extends Fragment implements Serializable {
    private static final String GROUPS_NAME = "Name";
    private static final String GROUPS_CATEGORY = "Category";
    private static final String GROUPS_ATHLETES = "Athletes";
    private static final String NUMBER_OF_ATHLETES = "NumberOfAthletes";

    private GroupInformationActivity activity;
    private LinearLayout expandableListOfMembers;
    private TextView membersOfGroupText, categoryOfGroup, nameOfGroup, numberOfAthletes;
    private CardView membersOfGroup;
    private TriathlonClub club;
    private List<String> dateOfAttendances;
    private final Map<String, List<String>> schedule;
    private String selectedGroup;

    public GroupInformationFragment() {this.schedule = new HashMap<>(); }

    public GroupInformationFragment(GroupInformationActivity activity) {
        this.activity = activity;
        this.schedule = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.selectedGroup =  requireActivity().getIntent().getExtras().getString("GROUP_NAME");

        this.expandableListOfMembers = view.findViewById(R.id.expandableListOfMembers);
        this.membersOfGroupText = view.findViewById(R.id.membersOfGroupText);
        this.membersOfGroup = view.findViewById(R.id.membersOfGroupInformation);
        this.categoryOfGroup = view.findViewById(R.id.categoryOfGroupInformation);
        this.nameOfGroup = view.findViewById(R.id.nameOfGroupInformation);
        this.numberOfAthletes = view.findViewById(R.id.numberOfAthletesInGroup);

        this.initializeAttendanceDates();

        ExpandableListView expandableAttendance = view.findViewById(R.id.attendanceOfGroupInformation);
        expandableAttendance.setAdapter(new AdapterOfExpendableAttendance(this.activity, this.dateOfAttendances, this.schedule));
        expandableAttendance.setGroupIndicator(null);

        this.getGroupInformation();
    }

    public void showMembersOfGroupInFragment(View view) {
        AutoTransition autoTransition;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            autoTransition = new AutoTransition();
            autoTransition.setDuration(0);
            TransitionManager.beginDelayedTransition(this.membersOfGroup, autoTransition);
        }

        if (this.expandableListOfMembers.getVisibility() == View.GONE)
            this.expandableListOfMembers.setVisibility(View.VISIBLE);
        else
            this.expandableListOfMembers.setVisibility(View.GONE);
    }

    public void getGroupInformation() {
        Map<String, Object> groupInformation = new HashMap<>();

        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getName().equals(selectedGroup)) {
                groupInformation.put(GROUPS_NAME, group.getName());
                groupInformation.put(GROUPS_CATEGORY, group.getCategory());
                groupInformation.put(NUMBER_OF_ATHLETES, group.getNumberOfAthletes());
                groupInformation.put(GROUPS_ATHLETES, group.getAthletesOfGroup());

                fillInGroupInformation(groupInformation);
            }
        }
    }

    public void fillInGroupInformation(Map<String, Object> groupInformation) {
        this.nameOfGroup.setText(Objects.requireNonNull(groupInformation.get(GROUPS_NAME)).toString());
        this.categoryOfGroup.setText(Objects.requireNonNull(groupInformation.get(GROUPS_CATEGORY)).toString());
        this.numberOfAthletes.setText(Objects.requireNonNull(groupInformation.get(NUMBER_OF_ATHLETES)).toString());

        ArrayList<Athlete> athletesOfGroup = (ArrayList<Athlete>) groupInformation.get(GROUPS_ATHLETES);
        Athlete lastAthlete = athletesOfGroup.get(athletesOfGroup.size() - 1);
        StringBuilder listOfAthletes = new StringBuilder();
        assert athletesOfGroup != null;
        for (Athlete athlete : athletesOfGroup) {
            if (athlete.getFullName().equals(lastAthlete.getFullName()))
                listOfAthletes.append(athlete.getFullName());
            else
                listOfAthletes.append(athlete.getFullName()).append("\n");
        }

        this.membersOfGroupText.setText(listOfAthletes.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_information, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeAttendanceDates () {
        this.dateOfAttendances = new ArrayList<>();
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            if (attendanceData.getGroup().getName().equals(selectedGroup)) {
                String trainingData = attendanceData.getDate() + " " + attendanceData.getTime() + " " + attendanceData.getSport();
                this.dateOfAttendances.add(trainingData);

                this.schedule.put(trainingData, new ArrayList<>());
                for (Athlete athlete : attendanceData.getAttendedAthletes())
                    this.schedule.computeIfAbsent(trainingData, k -> new ArrayList<>()).add(athlete.getFullName());
            }
        }
    }
}