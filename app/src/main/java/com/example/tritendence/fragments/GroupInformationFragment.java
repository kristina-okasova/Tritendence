package com.example.tritendence.fragments;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.activities.GroupInformationActivity;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.adapters.AdapterOfExpendableAttendance;
import com.example.tritendence.model.adapters.AdapterOfExpendableTrainingUnits;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupInformationFragment extends Fragment implements Serializable {
    private GroupInformationActivity activity;
    private LinearLayout expandableListOfMembers;
    private TextView membersOfGroupText, categoryOfGroup, nameOfGroup, numberOfAthletes;
    private CardView membersOfGroup;
    private TriathlonClub club;
    private List<String> dateOfAttendances, sportTypes;
    private final Map<String, List<String>> schedule;
    private Map<String, List<String>> timetable;
    private String selectedGroup, signedUser;
    private AdapterOfExpendableAttendance adapterOfAttendance;
    private AdapterOfExpendableTrainingUnits adapterOfTimetable;

    public GroupInformationFragment() {this.schedule = new HashMap<>(); }

    public GroupInformationFragment(GroupInformationActivity activity) {
        this.activity = activity;
        this.schedule = new HashMap<>();
        this.timetable = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.selectedGroup =  requireActivity().getIntent().getExtras().getString(getString(R.string.GROUP_NAME_EXTRA));

        this.expandableListOfMembers = view.findViewById(R.id.expandableListOfMembers);
        this.membersOfGroupText = view.findViewById(R.id.membersOfGroupText);
        this.membersOfGroup = view.findViewById(R.id.membersOfGroupInformation);
        this.categoryOfGroup = view.findViewById(R.id.categoryOfGroupInformation);
        this.nameOfGroup = view.findViewById(R.id.nameOfGroupInformation);
        this.numberOfAthletes = view.findViewById(R.id.numberOfAthletesInGroup);

        this.findTypeOfUser(view);
        this.initializeAttendanceDates();
        this.initializeTrainingUnits();

        ExpandableListView expandableAttendance = view.findViewById(R.id.attendanceOfGroupInformation);
        this.adapterOfAttendance = new AdapterOfExpendableAttendance(this.activity, this.dateOfAttendances, this.schedule);
        expandableAttendance.setAdapter(this.adapterOfAttendance);
        expandableAttendance.setGroupIndicator(null);

        ExpandableListView expandableTimetable = view.findViewById(R.id.timetableOfGroupInformation);
        this.adapterOfTimetable = new AdapterOfExpendableTrainingUnits(this.activity, this.sportTypes, this.timetable);
        expandableTimetable.setAdapter(this.adapterOfTimetable);
        expandableTimetable.setGroupIndicator(null);

        this.fillGroupInformation();
    }

    private void findTypeOfUser(View view) {
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser)) {
            ImageView deleteGroupIcon = view.findViewById(R.id.deleteGroupIcon);
            ImageView editGroupIcon = view.findViewById(R.id.editGroupIcon);

            deleteGroupIcon.setVisibility(View.VISIBLE);
            editGroupIcon.setVisibility(View.VISIBLE);
        }
    }

    public void showMembersOfGroupInFragment() {
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

    public void fillGroupInformation() {
        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getName().equals(selectedGroup)) {
                this.nameOfGroup.setText(group.getName());
                this.categoryOfGroup.setText(group.getCategoryTranslation());
                this.numberOfAthletes.setText(String.valueOf(group.getNumberOfAthletes()));
                ArrayList<Athlete> athletesOfGroup = group.getAthletesOfGroup();

                Athlete lastAthlete = null;
                if (athletesOfGroup.size() != 0)
                    lastAthlete = athletesOfGroup.get(athletesOfGroup.size() - 1);
                StringBuilder listOfAthletes = new StringBuilder();
                for (Athlete athlete : athletesOfGroup) {
                    if (lastAthlete != null && athlete.getFullName().equals(lastAthlete.getFullName()))
                        listOfAthletes.append(athlete.getFullName());
                    else
                        listOfAthletes.append(athlete.getFullName()).append("\n");
                }

                this.membersOfGroupText.setText(listOfAthletes.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_information, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initializeAttendanceDates () {
        this.dateOfAttendances = new ArrayList<>();
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            if (attendanceData.getGroup().getName().equals(selectedGroup)) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String trainingData = String.format(attendanceData.getDate(), format) + " " + attendanceData.getTime() + " " + attendanceData.getSport();
                this.dateOfAttendances.add(trainingData);

                this.schedule.put(trainingData, new ArrayList<>());
                for (Athlete athlete : attendanceData.getAttendedAthletes())
                    this.schedule.computeIfAbsent(trainingData, k -> new ArrayList<>()).add(athlete.getFullName());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initializeTrainingUnits () {
        this.initializeSports();
        Group group = this.findSelectedGroup();
        for (TrainingUnit unit : group.getTimetable()) {
            String unitData = unit.getDay() + " " + unit.getTime() + "\n" + unit.getSportTranslation() + ", " + unit.getLocation();
            this.timetable.computeIfAbsent(unit.getSportTranslation(), k -> new ArrayList<>()).add(unitData);
        }
    }

    private void initializeSports() {
        this.timetable.put(getString(R.string.SWIMMING), new ArrayList<>());
        this.timetable.put(getString(R.string.ATHLETICS), new ArrayList<>());
        this.timetable.put(getString(R.string.CYCLING), new ArrayList<>());
        this.timetable.put(getString(R.string.STRENGTH), new ArrayList<>());
        this.timetable.put(getString(R.string.OTHER), new ArrayList<>());

        this.sportTypes = new ArrayList<>();
        this.sportTypes.add(getString(R.string.SWIMMING));
        this.sportTypes.add(getString(R.string.ATHLETICS));
        this.sportTypes.add(getString(R.string.CYCLING));
        this.sportTypes.add(getString(R.string.STRENGTH));
        this.sportTypes.add(getString(R.string.OTHER));
    }

    public void deleteGroup() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        Group groupToDelete = this.findSelectedGroup();
        groupToDelete.setCategory("-1");
        root.child(getString(R.string.GROUP_CHILD_DB)+ "/" + Objects.requireNonNull(groupToDelete).getID()).child(getString(R.string.CATEGORY_DB)).setValue(-1);
        for (Athlete athlete : groupToDelete.getAthletesOfGroup())
            athlete.setGroupID(0);

        LoadData loadData = (LoadData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        Intent homePage = new Intent(this.getContext(), HomeActivity.class);
        homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), loadData);
        homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), R.id.groupsFragment);
        startActivity(homePage);
    }

    public Group findSelectedGroup() {
        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getName().equals(this.selectedGroup))
                return group;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.initializeAttendanceDates();
        this.adapterOfAttendance.notifyDataSetChanged();

        this.initializeTrainingUnits();
        this.adapterOfTimetable.notifyDataSetChanged();
    }
}