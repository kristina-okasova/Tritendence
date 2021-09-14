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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.tritendence.R;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Member;

import java.util.ArrayList;
import java.util.HashMap;

public class AthletesFragment extends Fragment {
    private ListView listOfAthletes;
    private ArrayList<HashMap<String, Object>> dataForListOfAthletes;
    private TriathlonClub club;
    private String signedUser;
    private SimpleAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.listOfAthletes = view.findViewById(R.id.listOfAthletes);
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));

        this.findTypeOfUser(view);
        this.getAlphabetSortingOfAthletes();
    }

    private void findTypeOfUser(View view) {
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser)) {
            ImageView addAthleteIcon = view.findViewById(R.id.addAthleteIcon);

            addAthleteIcon.setVisibility(View.VISIBLE);
        }
    }

    private void displayAthletes() {
        String[] insertingData = {getString(R.string.NAME_OF_ATHLETE_ADAPTER), getString(R.string.ICON_OF_ATHLETE_ADAPTER)};
        int[] UIData = {R.id.nameOfAthleteInList, R.id.iconToAthleteName};
        this.adapter = new SimpleAdapter(getActivity(), this.dataForListOfAthletes, R.layout.athlete_in_list_of_athletes, insertingData, UIData);
        this.listOfAthletes.setAdapter(this.adapter);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getAlphabetSortingOfAthletes() {
        this.dataForListOfAthletes = new ArrayList<>();
        ArrayList<Member> athletesNames = this.club.getAthletesSortedByAlphabet();
        int[] athletesImageIDs = new int[this.club.getNumberOfAthletes()];
        for (int i = 0; i < this.club.getNumberOfAthletes(); i++)
            athletesImageIDs[i] = R.drawable.athletes_icon;

        for (int i = 0; i < this.club.getNumberOfAthletes(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.NAME_OF_ATHLETE_ADAPTER), athletesNames.get(i).getFullName());
            mappedData.put(getString(R.string.ICON_OF_ATHLETE_ADAPTER), athletesImageIDs[i]);

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

            mappedData.put(getString(R.string.NAME_OF_ATHLETE_ADAPTER), athletesNames.get(i).getFullName());
            mappedData.put(getString(R.string.ICON_OF_ATHLETE_ADAPTER), athletesImageIDs[i]);

            this.dataForListOfAthletes.add(mappedData);
        }

        this.displayAthletes();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.getAlphabetSortingOfAthletes();
    }
}