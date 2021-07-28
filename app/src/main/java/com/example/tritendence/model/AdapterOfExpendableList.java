package com.example.tritendence.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.tritendence.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdapterOfExpendableList extends BaseExpandableListAdapter {
    private Activity context;
    private Map<String, List<String>> timetable;
    private List<String> daysOfTheWeek;

    public AdapterOfExpendableList(Activity context, List<String> daysOfTheWeek, Map<String, List<String>> timetable) {
        this.context = context;
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

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
