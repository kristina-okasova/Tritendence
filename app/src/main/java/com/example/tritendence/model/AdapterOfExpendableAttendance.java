package com.example.tritendence.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.tritendence.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdapterOfExpendableAttendance extends BaseExpandableListAdapter {
    private final Activity context;
    private final Map<String, List<String>> schedule;
    private final List<String> dateOfAttendance;

    public AdapterOfExpendableAttendance(Activity context, List<String> dateOfAttendances, Map<String, List<String>> schedule) {
        this.context = context;
        this.dateOfAttendance = dateOfAttendances;
        this.schedule = schedule;
    }

    @Override
    public int getGroupCount() {
        return this.schedule.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(this.schedule.get(this.dateOfAttendance.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.dateOfAttendance.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.schedule.get(this.dateOfAttendance.get(groupPosition))).get(childPosition);
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
        String trainingData = this.dateOfAttendance.get(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_view_group_name, null);
        }

        TextView dateData = convertView.findViewById(R.id.groupName);
        dateData.setText(trainingData);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String athlete = Objects.requireNonNull(this.schedule.get(this.dateOfAttendance.get(groupPosition))).get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.data_in_expandable_list_view, null);
        }

        TextView athleteName = convertView.findViewById(R.id.data);
        athleteName.setText(athlete);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
