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
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.LoadData;
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

//Adapter for expandable list of timetable provided by attendance fragment.
public class AdapterOfExpendableList extends BaseExpandableListAdapter {
    private final Activity context;
    private final Map<String, List<String>> timetable;
    private final List<String> daysOfTheWeek;
    private final TriathlonClub club;
    private final LoadData loadData;
    private TrainingUnit selectedUnit;

    public AdapterOfExpendableList(Activity context, List<String> daysOfTheWeek, Map<String, List<String>> timetable, TriathlonClub club, LoadData loadData) {
        this.context = context;
        this.daysOfTheWeek = daysOfTheWeek;
        this.timetable = timetable;
        this.club = club;
        this.loadData = loadData;
    }

    //Getters for counts.
    @Override
    public int getGroupCount() {
        return this.timetable.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int numberOfChildren = Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).size();
        //Displaying Toast when there are no children in the group to be displayed.
        if (numberOfChildren == 0)
            Toast.makeText(this.context, this.context.getString(R.string.NO_TRAININGS), Toast.LENGTH_LONG).show();

        return numberOfChildren;
    }

    //Getters for specific object of expandable list.
    @Override
    public Object getGroup(int groupPosition) {
        return this.daysOfTheWeek.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).get(childPosition);
    }

    //Getters for IDs of objects of expandable list.
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

    //Getters for displaying objects of expandable list.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Getting day of the week for the timetable.
        String day = this.daysOfTheWeek.get(groupPosition);

        //Creating new view of pre-defined layout.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.day_of_the_week, null);
        }

        //Getting monday of current week.
        LocalDate date = LocalDate.now();
        LocalDate monday;
        if (!date.getDayOfWeek().toString().equals(this.context.getString(R.string.MONDAY_DATE)))
            monday = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        else
            monday = date;

        //Shifting date by value defined by group position to get date of training unit.
        date = monday.plusDays(groupPosition);

        //Setting text to the created view.
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        TextView dayName = convertView.findViewById(R.id.dayName);
        dayName.setText(String.format("%s :  %s", date.format(format), day));

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Getting name of group in the timetable.
        String group = Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).get(childPosition);

        //Creating new view of pre-defined layout.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_data_in_expandable_list_view, null);
        }

        //Getting monday of current week.
        LocalDate date = LocalDate.now();
        LocalDate monday;
        if (!date.getDayOfWeek().toString().equals(this.context.getString(R.string.MONDAY_DATE)))
            monday = date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        else
            monday = date;

        //Shifting date by value defined by group position to get date of training unit.
        date = monday.plusDays(groupPosition);

        //Setting text to the created view.
        TextView groupName = convertView.findViewById(R.id.data);
        groupName.setText(group);

        //Setting on click listener to created view to display attendance sheet to the selected training unit.
        LocalDate finalDate = date;
        convertView.setOnClickListener(v -> {
            //Getting information about the group.
            String trainingTime = group.substring(0, group.indexOf(" "));
            String selectedGroupName = group.substring(group.indexOf(" ", group.indexOf(" ") + 1) + 1);

            //Finding group by extracted information.
            this.findGroupInfo(selectedGroupName, trainingTime, finalDate);
        });

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void findGroupInfo(String groupName, String time, LocalDate date) {
        String signedUser = this.context.getIntent().getExtras().getString(this.context.getString(R.string.SIGNED_USER_EXTRA));

        //Finding selected group by its name.
        Group selectedGroup = null;
        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getName().equals(groupName))
                selectedGroup = group;
        }

        //Finding selected training unit by its time.
        for (TrainingUnit unit : Objects.requireNonNull(selectedGroup).getTimetable()) {
            if (unit.getTime().equals(time))
                selectedUnit = unit;
        }

        //Checking if the attendance sheet has been filled before.
        AttendanceData attendanceData = this.checkFilledAttendance(selectedUnit, date);
        //If the attendance sheet has been filled before than display all accessible information.
        if (attendanceData != null)
            this.loadFilledAttendancePage(signedUser, attendanceData);
        //Otherwise provide attendance sheet to fill.
        else
            this.loadAttendanceSheetPage(selectedGroup, signedUser, date);
    }

    private AttendanceData checkFilledAttendance(TrainingUnit unit, LocalDate date) {
        //Checking if the attendance sheet has already been filled before.
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            if (attendanceData.getSport().equals(unit.getSportTranslation()) && attendanceData.getTime().equals(unit.getTime()) && attendanceData.getDate().equals(date.toString()) && attendanceData.getGroup().getID() == unit.getGroupID())
                return attendanceData;
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadFilledAttendancePage(String signedUser, AttendanceData attendanceData) {
        //Creating intent of Filled Attendance Sheet activity to display all accessible information about filled attendance.
        Intent filledAttendanceSheetPage = new Intent(context, FilledAttendanceSheetActivity.class);
        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.SIGNED_USER_EXTRA), signedUser);
        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.SPORT_SELECTION_EXTRA), ((HomeActivity) this.context).getSportSelection());
        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.THEME_EXTRA), ((HomeActivity) this.context).getThemesName());
        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.ATTENDANCE_DATA_EXTRA), attendanceData);
        filledAttendanceSheetPage.putExtra(this.context.getString(R.string.TRAINING_UNIT_EXTRA), this.selectedUnit);
        context.startActivity(filledAttendanceSheetPage);
        context.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadAttendanceSheetPage(Group group, String signedUser, LocalDate date) {
        //Creating intent of Attendance Sheet activity to load attendance sheet.
        Intent attendanceSheetPage = new Intent(context, AttendanceSheetActivity.class);
        attendanceSheetPage.putExtra(this.context.getString(R.string.GROUP_EXTRA), group);
        attendanceSheetPage.putExtra(this.context.getString(R.string.SIGNED_USER_EXTRA), signedUser);
        attendanceSheetPage.putExtra(this.context.getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        attendanceSheetPage.putExtra(this.context.getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        attendanceSheetPage.putExtra(this.context.getString(R.string.SPORT_SELECTION_EXTRA), ((HomeActivity) this.context).getSportSelection());
        attendanceSheetPage.putExtra(this.context.getString(R.string.THEME_EXTRA), ((HomeActivity) this.context).getThemesName());
        attendanceSheetPage.putExtra(this.context.getString(R.string.THEME_EXTRA), ((HomeActivity) this.context).getThemesName());
        attendanceSheetPage.putExtra(this.context.getString(R.string.TRAINING_UNIT_EXTRA), this.selectedUnit);
        attendanceSheetPage.putExtra(this.context.getString(R.string.DATE_EXTRA), date);
        context.startActivity(attendanceSheetPage);
        context.finish();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
