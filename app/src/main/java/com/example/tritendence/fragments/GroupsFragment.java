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
    //Intent's extras.
    private TriathlonClub club;
    private String signedUser;

    private ListView listOfGroups;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));

        this.listOfGroups = view.findViewById(R.id.listOfGroups);
        //Finding type of user and displaying accurate layout items and list of all groups.
        this.findTypeOfUser(view);
        this.displayGroups();
    }

    private void findTypeOfUser(View view) {
        //If signed user is admin then show imageview to add new group.
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser)) {
            ImageView addGroupIcon = view.findViewById(R.id.addGroupIcon);

            addGroupIcon.setVisibility(View.VISIBLE);
        }
    }

    private void displayGroups() {
        ArrayList<HashMap<String, Object>> dataForListOfGroups = new ArrayList<>();
        //Initializing icon to every group's name.
        int[] groupsImageIDs = new int[this.club.getNumberOfGroups()];
        for (int i = 0; i < this.club.getNumberOfGroups(); i++)
            groupsImageIDs[i] = R.drawable.groups_icon;

        //Creating hashmap consisting of the group's name and icon for adapter.
        for (int i = 0; i < this.club.getNumberOfGroups(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.NAME_OF_GROUP_ADAPTER), this.club.getGroupAtIndex(i).getName());
            mappedData.put(getString(R.string.ICON_OF_GROUP_ADAPTER), groupsImageIDs[i]);

            dataForListOfGroups.add(mappedData);
        }

        //Linking keys of hashmap to items of the layout used by adapter.
        String[] insertingData = {getString(R.string.NAME_OF_GROUP_ADAPTER), getString(R.string.ICON_OF_GROUP_ADAPTER)};
        int[] UIData = {R.id.nameOfGroupInList, R.id.iconToGroupName};

        //Creating adapter and setting it to the list of groups.
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfGroups, R.layout.group_in_list_of_groups, insertingData, UIData);
        this.listOfGroups.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.displayGroups();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }
}
