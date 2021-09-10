package com.example.tritendence.model.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.tritendence.R;
import com.example.tritendence.activities.AttendanceSheetActivity;
import com.example.tritendence.activities.FilledAttendanceSheetActivity;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdapterOfExpendableList extends BaseExpandableListAdapter {
    private final Activity context;
    private final Map<String, List<String>> timetable;
    private final List<String> daysOfTheWeek;
    private final TriathlonClub club;
    private TrainingUnit selectedUnit;

    public AdapterOfExpendableList(Activity context, List<String> daysOfTheWeek, Map<String, List<String>> timetable, TriathlonClub club) {
        this.context = context;
        this.daysOfTheWeek = daysOfTheWeek;
        this.timetable = timetable;
        this.club = club;
    }

    @Override
    public int getGroupCount() {
        return this.timetable.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int numberOfChildren = Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).size();
        if (numberOfChildren == 0)
            Toast.makeText(this.context, this.context.getString(R.string.NO_TRAININGS), Toast.LENGTH_LONG).show();

        return numberOfChildren;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.daysOfTheWeek.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String day = this.daysOfTheWeek.get(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.day_of_the_week, null);
        }

        LocalDate date = LocalDate.now();
        LocalDate monday;
        if (!date.getDayOfWeek().toString().equals(this.context.getString(R.string.MONDAY_DATE)))
            monday = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        else
            monday = date;

        date = monday.plusDays(groupPosition);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        TextView dayName = convertView.findViewById(R.id.dayName);
        dayName.setText(String.format("%s :  %s", date.format(format), day));

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String group = Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_data_in_expandable_list_view, null);
        }

        LocalDate date = LocalDate.now();
        LocalDate monday;
        if (!date.getDayOfWeek().toString().equals(this.context.getString(R.string.MONDAY_DATE)))
            monday = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        else
            monday = date;

        date = monday.plusDays(groupPosition);
        TextView groupName = convertView.findViewById(R.id.data);
        groupName.setText(group);

        LocalDate finalDate = date;
        convertView.setOnClickListener(v -> {
            String trainingTime = group.substring(0, group.indexOf(" "));
            String selectedGroupName = group.substring(group.indexOf(" ", group.indexOf(" ") + 1) + 1);
            this.findGroupInfo(selectedGroupName, trainingTime, groupName, finalDate);
        });

        return convertView;
    }

    private AttendanceData checkFilledAttendance(TrainingUnit unit, LocalDate date) {
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            if (attendanceData.getSport().equals(unit.getSportTranslation()) && attendanceData.getTime().equals(unit.getTime()) && attendanceData.getDate().equals(date.toString()) && attendanceData.getGroup().getID() == unit.getGroupID())
                return attendanceData;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void findGroupInfo(String groupName, String time, TextView groupView, LocalDate date) {
        String signedUser = this.context.getIntent().getExtras().getString(this.context.getString(R.string.SIGNED_USER_EXTRA));

        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getName().equals(groupName)) {
                for (TrainingUnit unit : group.getTimetable()) {
                    if (unit.getTime().equals(time))
                        selectedUnit = unit;
                }

                //groupView.setOnClickListener(v -> {
                    AttendanceData attendanceData = this.checkFilledAttendance(selectedUnit, date);
                    if (attendanceData != null) {
                        Intent filledAttendanceSheetPage = new Intent(context, FilledAttendanceSheetActivity.class);
                        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.SIGNED_USER_EXTRA), signedUser);
                        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.TRIATHLON_CLUB_EXTRA), club);
                        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.ATTENDANCE_DATA_EXTRA), attendanceData);
                        context.startActivity(filledAttendanceSheetPage);
                        context.finish();
                    }
                    else {
                        Intent attendanceSheetPage = new Intent(context, AttendanceSheetActivity.class);
                        attendanceSheetPage.putExtra(this.context.getString(R.string.GROUP_EXTRA), group);
                        attendanceSheetPage.putExtra(this.context.getString(R.string.SIGNED_USER_EXTRA), signedUser);
                        attendanceSheetPage.putExtra(this.context.getString(R.string.TRIATHLON_CLUB_EXTRA), club);
                        attendanceSheetPage.putExtra(this.context.getString(R.string.TRAINING_UNIT_EXTRA), selectedUnit);
                        attendanceSheetPage.putExtra(this.context.getString(R.string.DATE_EXTRA), date);
                        context.startActivity(attendanceSheetPage);
                        context.finish();
                    }
                //});
            }
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}