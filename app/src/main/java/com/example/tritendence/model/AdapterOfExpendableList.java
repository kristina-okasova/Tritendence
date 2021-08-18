package com.example.tritendence.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tritendence.R;
import com.example.tritendence.activities.AttendanceSheetActivity;
import com.example.tritendence.model.groups.Group;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String day = this.daysOfTheWeek.get(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.day_of_the_week, null);
        }

        TextView dayName = convertView.findViewById(R.id.dayName);
        dayName.setTypeface(null, Typeface.BOLD);
        dayName.setText(day);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String group = Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.data_in_expandable_list_view, null);
        }

        TextView groupName = convertView.findViewById(R.id.data);
        groupName.setText(group);

        String trainingTime = group.substring(0, group.indexOf(" "));
        String selectedGroupName = group.substring(group.indexOf(" ", group.indexOf(" ") + 1) + 1);
        this.findGroupInfo(selectedGroupName, trainingTime, groupName);

        return convertView;
    }

    private void findGroupInfo(String groupName, String time, TextView groupView) {
        String signedUser = this.context.getIntent().getExtras().getString("SIGNED_USER");

        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getName().equals(groupName)) {
                for (TrainingUnit unit : group.getTimetable()) {
                    if (unit.getTime().equals(time))
                        selectedUnit = unit;
                }
                groupView.setOnClickListener(v -> {
                    Intent attendanceSheetPage = new Intent(context, AttendanceSheetActivity.class);
                    attendanceSheetPage.putExtra("GROUP", group);
                    attendanceSheetPage.putExtra("SIGNED_USER", signedUser);
                    attendanceSheetPage.putExtra("TRIATHLON_CLUB", club);
                    attendanceSheetPage.putExtra("TRAINING_UNIT", selectedUnit);
                    context.startActivity(attendanceSheetPage);
                    context.finish();
                });
            }
        }
        /*DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(GROUPS_CHILD_DATABASE);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfGroups = (int) snapshot.getChildrenCount();

                for (int i = 1; i <= numberOfGroups; i++) {
                    String groupID = String.valueOf(i);
                    String groupName = (String) snapshot.child(groupID).child("Name").getValue();

                    if (group.equals(groupName)) {
                        groupView.setOnClickListener(v -> {
                            System.out.println("on click");
                            Intent attendanceSheetPage = new Intent(context, AttendanceSheetActivity.class);
                            attendanceSheetPage.putExtra("GROUP_ID", groupID);
                            attendanceSheetPage.putExtra("GROUP_NAME", groupName);
                            attendanceSheetPage.putExtra("TRAINING_TIME", time);
                            attendanceSheetPage.putExtra("SPORT_TYPE", sport);
                            attendanceSheetPage.putExtra("SIGNED_USER", trainersName);
                            attendanceSheetPage.putExtra("TRIATHLON_CLUB", club);
                            context.startActivity(attendanceSheetPage);
                            context.finish();
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });*/
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
