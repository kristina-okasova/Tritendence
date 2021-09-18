package com.example.tritendence.model.adapters;

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

//Adapter for expandable list of attendances provided by group information fragment.
public class AdapterOfExpendableAttendance extends BaseExpandableListAdapter {
    private final Activity context;
    private final Map<String, List<String>> schedule;
    private final List<String> dateOfAttendance;

    public AdapterOfExpendableAttendance(Activity context, List<String> dateOfAttendances, Map<String, List<String>> schedule) {
        this.context = context;
        this.dateOfAttendance = dateOfAttendances;
        this.schedule = schedule;
    }

    //Getters for counts.
    @Override
    public int getGroupCount() {
        return this.schedule.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(this.schedule.get(this.dateOfAttendance.get(groupPosition))).size();
    }

    //Getters for specific object of expandable list.
    @Override
    public Object getGroup(int groupPosition) {
        return this.dateOfAttendance.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.schedule.get(this.dateOfAttendance.get(groupPosition))).get(childPosition);
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
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Getting attendance data about training unit.
        String trainingData = this.dateOfAttendance.get(groupPosition);

        //Creating new view of pre-defined layout.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_data_in_expandable_list_view, null);
        }

        //Setting text to the created view.
        TextView dateData = convertView.findViewById(R.id.data);
        dateData.setText(trainingData);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Getting name of attended athlete.
        String athlete = Objects.requireNonNull(this.schedule.get(this.dateOfAttendance.get(groupPosition))).get(childPosition);

        //Creating new view of pre-defined layout.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_data_in_expandable_list_view, null);
        }

        //Setting text to the created view.
        TextView athleteName = convertView.findViewById(R.id.data);
        athleteName.setText(athlete);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
