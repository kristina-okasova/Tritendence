package com.example.tritendence.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.adapters.AdapterOfExpendableList;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Admin;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AttendanceFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String signedUser, sportSelection;

    private HomeActivity activity;
    private List<String> daysOfTheWeek;
    private final Map<String, List<String>> timetable;
    private ExpandableListView expandableTimetable;
    private Member signedTrainer;

    public AttendanceFragment() {
        this.timetable = new HashMap<>();
    }

    public AttendanceFragment(HomeActivity activity) {
        this.timetable = new HashMap<>();
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.loadData = (LoadData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        if (this.sportSelection == null)
            this.sportSelection = requireActivity().getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));

        //Finding signed trainer by name and initializing timetable.
        this.signedTrainer = this.findCurrentUser();
        this.initializeTimetable();
        this.initializeCurrentWeek(view);

        //Adding specific groups to timetable based on the current type of sport selection.
        this.addGroups(this.sportSelection);

        //Adding data to expandable timetable and setting on group expand listener.
        this.expandableTimetable = view.findViewById(R.id.timetable);
        this.updateExpandableTimetable();

        this.expandableTimetable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            //When a group expands, collapse the group that has been expanded before.
            public void onGroupExpand(int groupPosition) {
                if (previousGroup != -1 && groupPosition != previousGroup)
                    expandableTimetable.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    private Member findCurrentUser() {
        for (Member member : this.club.getMembersOfClub()) {
            if ((member instanceof Trainer || member instanceof Admin) && member.getFullName().equals(this.signedUser))
                return member;
        }
        return null;
    }

    private void initializeTimetable() {
        //Adding days of the week to timetable.
        this.daysOfTheWeek = new ArrayList<>();
        this.daysOfTheWeek.add(getString(R.string.MONDAY));
        this.daysOfTheWeek.add(getString(R.string.TUESDAY));
        this.daysOfTheWeek.add(getString(R.string.WEDNESDAY));
        this.daysOfTheWeek.add(getString(R.string.THURSDAY));
        this.daysOfTheWeek.add(getString(R.string.FRIDAY));
        this.daysOfTheWeek.add(getString(R.string.SATURDAY));
        this.daysOfTheWeek.add(getString(R.string.SUNDAY));

        this.clearTimetable();
    }

    private void clearTimetable() {
        //Clearing whole timetable by setting list of groups for every day of the week to empty list.
        this.timetable.put(getString(R.string.MONDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.TUESDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.WEDNESDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.THURSDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.FRIDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.SATURDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.SUNDAY), new ArrayList<>());
    }

    @SuppressLint("DefaultLocale")
    private void initializeCurrentWeek(View view) {
        TextView currentWeek = view.findViewById(R.id.currentWeek);
        currentWeek.setText(String.format("%s %d", getString(R.string.WEEK), this.club.getNumberOfWeek()));
    }

    public void updateExpandableTimetable() {
        //Creating adapter and setting it to the expandable list of attendance.
        this.expandableTimetable.setAdapter(new AdapterOfExpendableList(this.activity, this.daysOfTheWeek, this.timetable, this.club, this.loadData));
        this.expandableTimetable.setGroupIndicator(null);
    }

    public void addGroups(String sport) {
        //Adding groups to expandable timetable determined by current sport selection.
        for (Group group : this.club.getGroupsOfClub()) {
            for (TrainingUnit unit : group.getTimetable()) {
                if (unit.getSport().equals(sport) || sport.equals(getString(R.string.EMPTY_STRING)))
                    addGroupToParticularDay(group.getName(), unit.getDay(), unit.getTime(), unit.getSport());
            }
        }
    }

    private void addGroupToParticularDay(String name, String day, String time, String sport) {
        //Translating value of sport.
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
            case "Strength":
                sport = getString(R.string.STRENGTH);
                break;
            case "Other":
                sport = getString(R.string.OTHER);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sport);
        }

        //Adding information about training unit to specific day in the timetable.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            this.timetable.computeIfAbsent(day, k -> new ArrayList<>()).add(time + " " + sport + " " + name);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //Attaching options menu to the fragment.
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.attendance_selection_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.clearTimetable();

        //Determining the sport selection and displaying groups taking into account this decision.
        switch (item.getItemId()) {
            case R.id.swimmingSelection:
                this.sportSelection = getString(R.string.SWIMMING_DB);
                break;
            case R.id.athleticsSelection:
                this.sportSelection = getString(R.string.ATHLETICS_DB);
                break;
            case R.id.cyclingSelection:
                this.sportSelection = getString(R.string.CYCLING_DB);
                break;
            case R.id.allTrainings:
                this.sportSelection = getString(R.string.EMPTY_STRING);
                break;
            default:
                break;
        }
        this.addGroups(this.sportSelection);
        this.activity.updateSportSelection(this.sportSelection);

        //Updating timetable of the training units.
        this.updateExpandableTimetable();
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;

        //Updating values displayed in the timetable.
        this.initializeTimetable();
        this.addGroups(this.sportSelection);
        this.updateExpandableTimetable();
    }
}
