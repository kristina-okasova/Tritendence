package com.example.tritendence.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.tritendence.R;
import com.example.tritendence.model.TriathlonClub;

import java.util.ArrayList;
import java.util.HashMap;

public class AthletesFragment extends Fragment {
    private ListView listOfAthletes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.listOfAthletes = view.findViewById(R.id.listOfAthletes);
        this.displayAthletes();
    }

    private void displayAthletes() {
        TriathlonClub club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        ArrayList<HashMap<String, Object>> dataForListOfAthletes = new ArrayList<>();
        int[] athletesImageIDs = new int[club.getNumberOfAthletes()];
        for (int i = 0; i < club.getNumberOfAthletes(); i++)
            athletesImageIDs[i] = R.drawable.athletes_icon;

        for (int i = 0; i < club.getNumberOfAthletes(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put("nameOfAthlete", club.getAthleteAtIndex(i).getFullName());
            mappedData.put("iconOfAthlete", athletesImageIDs[i]);

            dataForListOfAthletes.add(mappedData);
        }

        String[] insertingData = {"nameOfAthlete", "iconOfAthlete"};
        int[] UIData = {R.id.nameOfAthleteInList, R.id.iconToAthleteName};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfAthletes, R.layout.athlete_in_list_of_athletes, insertingData, UIData);
        this.listOfAthletes.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_athletes, container, false);
    }
}