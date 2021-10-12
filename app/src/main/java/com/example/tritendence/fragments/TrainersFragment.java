package com.example.tritendence.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.example.tritendence.R;
import com.example.tritendence.model.lists.ListScrollable;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Member;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainersFragment extends Fragment {
    private ListScrollable listOfTrainers;
    private TriathlonClub club;
    private String signedUser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));

        this.findTypeOfUser(view);
        this.listOfTrainers = view.findViewById(R.id.listOfTrainers);
        //Displaying list of trainers.
        this.displayTrainers(this.club.getTrainersSortedByAlphabet());
    }

    private void findTypeOfUser(View view) {
        //If signed user is admin then show imageview to add new group.
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser)) {
            ImageView addTrainerIcon = view.findViewById(R.id.addTrainerIcon);

            addTrainerIcon.setVisibility(View.VISIBLE);
        }
    }

    private void displayTrainers(ArrayList<Member> trainersNames) {
        ArrayList<HashMap<String, Object>> dataForListOfTrainers = new ArrayList<>();
        //Initializing icon to every trainer's name.
        int[] trainersImageIDs = new int[this.club.getNumberOfTrainers()];
        for (int i = 0; i < this.club.getNumberOfTrainers(); i++)
            trainersImageIDs[i] = R.drawable.trainers_icon;

        //Creating hashmap consisting of the trainer's name and icon for adapter.
        for (int i = 0; i < this.club.getNumberOfTrainers(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.NAME_OF_TRAINER_ADAPTER), trainersNames.get(i).getFullName());
            mappedData.put(getString(R.string.ICON_OF_TRAINER_ADAPTER), trainersImageIDs[i]);

            dataForListOfTrainers.add(mappedData);
        }

        //Linking keys of hashmap to items of the layout used by adapter.
        String[] insertingData = {getString(R.string.NAME_OF_TRAINER_ADAPTER), getString(R.string.ICON_OF_TRAINER_ADAPTER)};
        int[] UIData = {R.id.nameOfTrainerInList, R.id.iconToTrainerName};

        //Creating adapter and setting it to the list of trainers.
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfTrainers, R.layout.trainer_in_list_of_trainers, insertingData, UIData);
        this.listOfTrainers.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //Attaching options menu to the fragment.
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.athletes_sort_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Determining the sorting method by the selected item from options menu.
        switch (item.getItemId()) {
            case R.id.alphabetSorting:
                this.displayTrainers(this.club.getTrainersSortedByAlphabet());
                break;
            case R.id.ageSorting:
                this.displayTrainers(this.club.getTrainersSortedByAge());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.displayTrainers(this.club.getTrainersSortedByAlphabet());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainers, container, false);
    }


}