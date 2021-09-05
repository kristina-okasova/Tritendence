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
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        Member signedTrainer = this.findCurrentUser();
        this.initializeTimetable();
        if (signedTrainer instanceof Trainer)
            this.addGroups(Objects.requireNonNull((Trainer) signedTrainer).getSportTranslation());
        else
            this.addGroups(getString(R.string.EMPTY_STRING));

        TextView currentWeek = view.findViewById(R.id.currentWeek);
        currentWeek.setText(String.format("%s %d", getString(R.string.WEEK), this.club.getNumberOfWeek()));

        this.expandableTimetable = view.findViewById(R.id.timetable);
        this.updateExpandableTimetable();

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

    private Member findCurrentUser() {
        String signedUserName = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        for (Member member : this.club.getMembersOfClub()) {
            if ((member instanceof Trainer || member instanceof Admin) && member.getFullName().equals(signedUserName))
                return member;
        }
        return null;
    }

    public void updateExpandableTimetable() {
        this.expandableTimetable.setAdapter(new AdapterOfExpendableList(this.activity, this.daysOfTheWeek, this.timetable, club));
        this.expandableTimetable.setGroupIndicator(null);
    }

    public void addGroups(String sport) {
        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getCategory().equals("-1"))
                continue;
            for (TrainingUnit unit : group.getTimetable()) {
                if (unit.getSport().equals(sport) || sport.equals(getString(R.string.EMPTY_STRING)))
                    addGroupToParticularDay(group.getName(), unit.getDay(), unit.getTime(), unit.getSport());
            }
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

        this.clearTimetable();
    }

    public void clearTimetable() {
        this.timetable.put(getString(R.string.MONDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.TUESDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.WEDNESDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.THURSDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.FRIDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.SATURDAY), new ArrayList<>());
        this.timetable.put(getString(R.string.SUNDAY), new ArrayList<>());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.attendance_selection_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.clearTimetable();

        switch (item.getItemId()) {
            case R.id.swimmingSelection:
                this.addGroups(getString(R.string.SWIMMING_DB));
                break;
            case R.id.athleticsSelection:
                this.addGroups(getString(R.string.ATHLETICS_DB));
                break;
            case R.id.cyclingSelection:
                this.addGroups(getString(R.string.CYCLING_DB));
                break;
            case R.id.allTrainings:
                this.addGroups(getString(R.string.EMPTY_STRING));
                break;
            default:
                break;
        }

        this.updateExpandableTimetable();
        return super.onOptionsItemSelected(item);
    }
}
