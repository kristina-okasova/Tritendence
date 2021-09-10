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
    private ArrayList<HashMap<String, Object>> dataForListOfTrainers;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.listOfTrainers = view.findViewById(R.id.listOfTrainers);
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.getAlphabetSortingOfTrainers();
    }

    private void getAlphabetSortingOfTrainers() {
        this.dataForListOfTrainers = new ArrayList<>();
        ArrayList<Member> trainersNames = this.club.getTrainersSortedByAlphabet();
        int[] trainersImageIDs = new int[this.club.getNumberOfTrainers()];
        for (int i = 0; i < this.club.getNumberOfTrainers(); i++)
            trainersImageIDs[i] = R.drawable.trainers_icon;

        for (int i = 0; i < this.club.getNumberOfTrainers(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.NAME_OF_TRAINER_ADAPTER), trainersNames.get(i).getFullName());
            mappedData.put(getString(R.string.ICON_OF_TRAINER_ADAPTER), trainersImageIDs[i]);

            this.dataForListOfTrainers.add(mappedData);
        }

        this.displayTrainers();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAgeSortingOfTrainers() {
        this.dataForListOfTrainers = new ArrayList<>();
        ArrayList<Member> trainersNames = this.club.getTrainersSortedByAge();
        int[] trainersImageIDs = new int[this.club.getNumberOfTrainers()];
        for (int i = 0; i < this.club.getNumberOfTrainers(); i++)
            trainersImageIDs[i] = R.drawable.trainers_icon;

        for (int i = 0; i < this.club.getNumberOfTrainers(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.NAME_OF_TRAINER_ADAPTER), trainersNames.get(i).getFullName());
            mappedData.put(getString(R.string.ICON_OF_TRAINER_ADAPTER), trainersImageIDs[i]);

            this.dataForListOfTrainers.add(mappedData);
        }

        this.displayTrainers();
    }

    private void displayTrainers() {
        String[] insertingData = {getString(R.string.NAME_OF_TRAINER_ADAPTER), getString(R.string.ICON_OF_TRAINER_ADAPTER)};
        int[] UIData = {R.id.nameOfTrainerInList, R.id.iconToTrainerName};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), this.dataForListOfTrainers, R.layout.trainer_in_list_of_trainers, insertingData, UIData);
        this.listOfTrainers.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.athletes_sort_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alphabetSorting:
                this.getAlphabetSortingOfTrainers();
                break;
            case R.id.ageSorting:
                this.getAgeSortingOfTrainers();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainers, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
    }
}