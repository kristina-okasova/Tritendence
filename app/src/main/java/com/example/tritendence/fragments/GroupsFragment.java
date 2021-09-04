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
import com.example.tritendence.model.TriathlonClub;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupsFragment extends Fragment {
    private ListView listOfGroups;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.listOfGroups = view.findViewById(R.id.listOfGroups);
        this.displayGroups();
    }

    private void displayGroups() {
        TriathlonClub club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        ArrayList<HashMap<String, Object>> dataForListOfGroups = new ArrayList<>();
        int[] groupsImageIDs = new int[club.getNumberOfGroups()];
        for (int i = 0; i < club.getNumberOfGroups(); i++)
            groupsImageIDs[i] = R.drawable.groups_icon;

        for (int i = 0; i < club.getNumberOfGroups(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.NAME_OF_GROUP_ADAPTER), club.getGroupAtIndex(i));
            mappedData.put(getString(R.string.ICON_OF_GROUP_ADAPTER), groupsImageIDs[i]);

            dataForListOfGroups.add(mappedData);
        }

        String[] insertingData = {getString(R.string.NAME_OF_GROUP_ADAPTER), getString(R.string.ICON_OF_GROUP_ADAPTER)};
        int[] UIData = {R.id.nameOfGroupInList, R.id.iconToGroupName};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfGroups, R.layout.group_in_list_of_groups, insertingData, UIData);
        this.listOfGroups.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }
}
