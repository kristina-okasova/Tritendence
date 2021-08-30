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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.tritendence.R;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AthletesFragment extends Fragment {
    private ListView listOfAthletes;
    private ArrayList<HashMap<String, Object>> dataForListOfAthletes;
    private TriathlonClub club;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.listOfAthletes = view.findViewById(R.id.listOfAthletes);
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.getAlphabetSortingOfAthletes();
    }

    private void displayAthletes() {
        String[] insertingData = {"nameOfAthlete", "iconOfAthlete"};
        int[] UIData = {R.id.nameOfAthleteInList, R.id.iconToAthleteName};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), this.dataForListOfAthletes, R.layout.athlete_in_list_of_athletes, insertingData, UIData);
        this.listOfAthletes.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_athletes, container, false);
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
                this.getAlphabetSortingOfAthletes();
                break;
            case R.id.ageSorting:
                this.getAgeSortingOfAthletes();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAlphabetSortingOfAthletes() {
        this.dataForListOfAthletes = new ArrayList<>();
        ArrayList<Member> athletesNames = this.club.getAthletesSortedByAlphabet();
        int[] athletesImageIDs = new int[this.club.getNumberOfAthletes()];
        for (int i = 0; i < this.club.getNumberOfAthletes(); i++)
            athletesImageIDs[i] = R.drawable.athletes_icon;

        for (int i = 0; i < this.club.getNumberOfAthletes(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put("nameOfAthlete", athletesNames.get(i).getFullName());
            mappedData.put("iconOfAthlete", athletesImageIDs[i]);

            this.dataForListOfAthletes.add(mappedData);
        }

        this.displayAthletes();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAgeSortingOfAthletes() {
        this.dataForListOfAthletes = new ArrayList<>();
        ArrayList<Member> athletesNames = this.club.getAthletesSortedByAge();
        int[] athletesImageIDs = new int[this.club.getNumberOfAthletes()];
        for (int i = 0; i < this.club.getNumberOfAthletes(); i++)
            athletesImageIDs[i] = R.drawable.athletes_icon;

        for (int i = 0; i < this.club.getNumberOfAthletes(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put("nameOfAthlete", athletesNames.get(i).getFullName());
            mappedData.put("iconOfAthlete", athletesImageIDs[i]);

            this.dataForListOfAthletes.add(mappedData);
        }

        this.displayAthletes();
    }
}