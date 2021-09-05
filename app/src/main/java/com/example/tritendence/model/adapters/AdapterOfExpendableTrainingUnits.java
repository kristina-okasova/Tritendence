package com.example.tritendence.model.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdapterOfExpendableTrainingUnits extends BaseExpandableListAdapter {
    private final Activity context;
    private final Map<String, List<String>> timetable;
    private final List<String> sportTypes;

    public AdapterOfExpendableTrainingUnits(Activity context, List<String> sportTypes, Map<String, List<String>> timetable) {
        this.context = context;
        this.sportTypes = sportTypes;
        this.timetable = timetable;
    }

    @Override
    public int getGroupCount() {
        return this.timetable.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int numberOfChildren = Objects.requireNonNull(this.timetable.get(this.sportTypes.get(groupPosition))).size();
        if (numberOfChildren == 0)
            Toast.makeText(this.context, this.context.getString(R.string.NO_TRAININGS_IN_CATEGORY), Toast.LENGTH_LONG).show();

        return numberOfChildren;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.sportTypes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.timetable.get(this.sportTypes.get(groupPosition))).get(childPosition);
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
        String sportData = this.sportTypes.get(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_data_in_expandable_list_view, null);
        }

        TextView sportType = convertView.findViewById(R.id.data);
        sportType.setText(sportData);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String athlete = Objects.requireNonNull(this.timetable.get(this.sportTypes.get(groupPosition))).get(childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_data_in_expandable_list_view, null);
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
