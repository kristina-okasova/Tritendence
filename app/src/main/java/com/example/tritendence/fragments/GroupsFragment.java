package com.example.tritendence.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupsFragment extends Fragment {
    private TriathlonClub club;
    private ListView listOfGroups;
    private String signedUser;
    private SimpleAdapter adapter;
    private ArrayList<HashMap<String, Object>> dataForListOfGroups;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));

        this.listOfGroups = view.findViewById(R.id.listOfGroups);
        this.findTypeOfUser(view);
        this.getListOfGroups();
    }

    private void findTypeOfUser(View view) {
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser)) {
            ImageView addGroupIcon = view.findViewById(R.id.addGroupIcon);

            addGroupIcon.setVisibility(View.VISIBLE);
        }
    }

    private void displayGroups() {
        String[] insertingData = {getString(R.string.NAME_OF_GROUP_ADAPTER), getString(R.string.ICON_OF_GROUP_ADAPTER)};
        int[] UIData = {R.id.nameOfGroupInList, R.id.iconToGroupName};
        this.adapter = new SimpleAdapter(getActivity(), this.dataForListOfGroups, R.layout.group_in_list_of_groups, insertingData, UIData);
        this.listOfGroups.setAdapter(adapter);
    }

    private void getListOfGroups() {
        this.dataForListOfGroups = new ArrayList<>();
        int[] groupsImageIDs = new int[club.getNumberOfGroups()];
        for (int i = 0; i < club.getNumberOfGroups(); i++)
            groupsImageIDs[i] = R.drawable.groups_icon;

        for (int i = 0; i < club.getNumberOfGroups(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            Group group = club.getGroupAtIndex(i);
            if (group.getCategory().equals("-1"))
                continue;
            mappedData.put(getString(R.string.NAME_OF_GROUP_ADAPTER), club.getGroupAtIndex(i).getName());
            mappedData.put(getString(R.string.ICON_OF_GROUP_ADAPTER), groupsImageIDs[i]);

            this.dataForListOfGroups.add(mappedData);
        }
        this.displayGroups();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.getListOfGroups();
    }
}
