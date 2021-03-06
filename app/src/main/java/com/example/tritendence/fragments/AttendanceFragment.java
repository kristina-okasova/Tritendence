package com.example.tritendence.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String sportSelection = null;

    private HomeActivity activity;
    private List<String> daysOfTheWeek;
    private final Map<String, List<String>> timetable;
    private ExpandableListView expandableTimetable;

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

        if (this.sportSelection == null)
            this.sportSelection = requireActivity().getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));

        //Finding signed trainer by name and initializing timetable.
        this.initializeTimetable();
        this.initializeCurrentWeek(view);
        this.loadData.setActivity(getActivity());

        //Adding specific groups to timetable based on the current type of sport selection.
        this.addGroups(this.sportSelection);

        //Adding data to expandable timetable and setting on group expand listener.
        this.expandableTimetable = view.findViewById(R.id.timetable);
        this.initializeExpandableTimetable();

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
        Calendar today = Calendar.getInstance();
        int numberOfWeek;
        if (today.get(Calendar.MONTH) > 7)
            numberOfWeek = today.get(Calendar.WEEK_OF_YEAR) - this.club.getFirstWeek() + 1;
        else
            numberOfWeek = today.get(Calendar.WEEK_OF_YEAR) + today.getActualMaximum
                    (Calendar.WEEK_OF_YEAR) - this.club.getFirstWeek() + 1;
        currentWeek.setText(String.format("%s %d", getString(R.string.WEEK), numberOfWeek));
    }

    public void initializeExpandableTimetable() {
        //Creating adapter and setting it to the expandable list of attendance.
        AdapterOfExpendableList adapterOfTimetable = new AdapterOfExpendableList(this.activity, this.daysOfTheWeek, this.timetable, this.club, this.loadData);
        this.expandableTimetable.setAdapter(adapterOfTimetable);
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

        this.sortGroupsByTime();
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

    private void sortGroupsByTime() {
        //Sorting  groups of specific day by the time of training.
        for (Map.Entry<String, List<String>> trainingsOfTheDay : this.timetable.entrySet()) {
            Collections.sort(trainingsOfTheDay.getValue(), (training1, training2) -> {
                String time1 = training1.substring(0, training1.indexOf(" "));
                String time2 = training2.substring(0, training2.indexOf(" "));

                return time1.compareTo(time2);
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //Attaching options menu to the fragment.
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.attendance_selection_menu, menu);

        List<String> optionsInMenu = Arrays.asList(getString(R.string.SWIMMING_SORTING), getString(R.string.ATHLETICS_SORTING), getString(R.string.CYCLING_SORTING), getString(R.string.ALL_SPORT_SORTING));
        for (int i = 0; i < 4; i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(optionsInMenu.get(i));
            s.setSpan(new ForegroundColorSpan(R.color.dark_text), 0, s.length(), 0);
            item.setTitle(s);
        }

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
        this.initializeExpandableTimetable();
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
    }
}
