package com.example.tritendence.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tritendence.R;
import com.example.tritendence.activities.AttendanceSheetActivity;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.fragments.AttendanceFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdapterOfExpendableList extends BaseExpandableListAdapter {
    private final static String GROUPS_CHILD_DATABASE = "Groups";

    private DatabaseReference database;
    private Activity context;
    private HomeActivity root;
    private AttendanceFragment fragment;
    private Map<String, List<String>> timetable;
    private List<String> daysOfTheWeek;

    public AdapterOfExpendableList(Activity context, HomeActivity root, AttendanceFragment fragment, List<String> daysOfTheWeek, Map<String, List<String>> timetable) {
        this.context = context;
        this.root = root;
        this.fragment = fragment;
        this.daysOfTheWeek = daysOfTheWeek;
        this.timetable = timetable;
    }

    @Override
    public int getGroupCount() {
        return this.timetable.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(this.timetable.get(this.daysOfTheWeek.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.daysOfTheWeek.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.timetable.get(this.daysOfTheWeek.get(groupPosition)).get(childPosition);
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

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String group = this.timetable.get(this.daysOfTheWeek.get(groupPosition)).get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_in_timetable, null);
        }

        TextView groupName = convertView.findViewById(R.id.groupName);
        groupName.setText(group);
        this.findGroupID(group.substring(group.indexOf(" ", group.indexOf(" ") + 1) + 1), groupName);

        return convertView;
    }

    private void findGroupID(String group, TextView groupView) {
        this.database = FirebaseDatabase.getInstance().getReference().child(GROUPS_CHILD_DATABASE);
        System.out.println("Group " + group);
        this.database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfGroups = (int) snapshot.getChildrenCount();

                for (int i = 1; i <= numberOfGroups; i++) {
                    String groupID = String.valueOf(i);
                    String groupName = (String) snapshot.child(groupID).child("Name").getValue();

                    if (group.equals(groupName)) {
                        groupView.setOnClickListener(v -> {
                            Intent attendanceSheetPage = new Intent(context, AttendanceSheetActivity.class);
                            System.out.println("intent " + groupID);
                            attendanceSheetPage.putExtra("GROUP_ID", groupID);
                            context.startActivity(attendanceSheetPage);
                            //context.finish();
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
