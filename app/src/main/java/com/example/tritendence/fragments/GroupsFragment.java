package com.example.tritendence.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupsFragment extends Fragment {
    private final static String GROUPS_CHILD_DATABASE = "Groups";
    private final static String GROUP_NAME = "Name";

    private ListView listOfGroups;
    private ArrayList<String> namesOfGroups;
    private DatabaseReference database;
    private SimpleAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.listOfGroups = view.findViewById(R.id.attendanceSheet);
        this.collectNamesOfGroups(view);
    }

    private void collectNamesOfGroups(View view) {
        this.database = FirebaseDatabase.getInstance().getReference().child(GROUPS_CHILD_DATABASE);

        this.database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                namesOfGroups = new ArrayList<>();
                int numberOfGroups = (int) snapshot.getChildrenCount();
                System.out.println("Number of groups: " + numberOfGroups);
                for (int i = 1; i <= numberOfGroups; i++) {
                    String groupID = String.valueOf(i);
                    namesOfGroups.add(snapshot.child(groupID).child(GROUP_NAME).getValue().toString());
                }

                displayGroups(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayGroups(View view) {
        ArrayList<HashMap<String, Object>> dataForListOfGroups = new ArrayList<>();
        int[] groupsImageIDs = new int[this.namesOfGroups.size()];
        for (int i = 0; i < this.namesOfGroups.size(); i++)
            groupsImageIDs[i] = R.drawable.groups_icon;

        this.listOfGroups = view.findViewById(R.id.listOfGroups);
        for (int i = 0; i < this.namesOfGroups.size(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put("nameOfGroup", this.namesOfGroups.get(i));
            mappedData.put("iconOfGroup", groupsImageIDs[i]);

            dataForListOfGroups.add(mappedData);
        }

        String[] insertingData = {"nameOfGroup", "iconOfGroup"};
        int[] UIData = {R.id.nameOfGroupInList, R.id.iconToGroupName};
        this.adapter = new SimpleAdapter(getActivity(), dataForListOfGroups, R.layout.group_in_list_of_groups, insertingData, UIData);
        this.listOfGroups.setAdapter(this.adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }
}
