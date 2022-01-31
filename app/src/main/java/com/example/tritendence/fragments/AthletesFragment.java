package com.example.tritendence.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AthletesFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private String signedUser;

    private ListView listOfAthletes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));

        this.listOfAthletes = view.findViewById(R.id.listOfAthletes);
        //Finding type of user and displaying accurate layout items and list of all athletes.
        this.findTypeOfUser(view);
        this.displayAthletes(this.club.getAthletesSortedByAlphabet());
    }

    private void findTypeOfUser(View view) {
        //If signed user is admin then show imageview to add new athlete.
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser)) {
            ImageView addAthleteIcon = view.findViewById(R.id.addAthleteIcon);

            addAthleteIcon.setVisibility(View.VISIBLE);
        }
    }

    private void displayAthletes(ArrayList<Member> athletesNames) {
        ArrayList<HashMap<String, Object>> dataForListOfAthletes = new ArrayList<>();
        //Initializing icon to every athlete's name.
        int[] athletesImageIDs = new int[this.club.getNumberOfAthletes()];
        for (int i = 0; i < this.club.getNumberOfAthletes(); i++)
            athletesImageIDs[i] = R.drawable.athletes_icon;

        //Creating hashmap consisting of the athlete's name and icon for adapter.
        for (int i = 0; i < this.club.getNumberOfAthletes(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();

            mappedData.put(getString(R.string.NAME_OF_ATHLETE_ADAPTER), athletesNames.get(i).getFullName());
            mappedData.put(getString(R.string.ICON_OF_ATHLETE_ADAPTER), athletesImageIDs[i]);

            dataForListOfAthletes.add(mappedData);
        }

        //Linking keys of hashmap to items of the layout used by adapter.
        String[] insertingData = {getString(R.string.NAME_OF_ATHLETE_ADAPTER), getString(R.string.ICON_OF_ATHLETE_ADAPTER)};
        int[] UIData = {R.id.nameOfAthleteInList, R.id.iconToAthleteName};

        //Creating adapter and setting it to the list of athletes.
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfAthletes, R.layout.athlete_in_list_of_athletes, insertingData, UIData);
        this.listOfAthletes.setAdapter(adapter);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //Attaching options menu to the fragment.
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.athletes_sort_menu, menu);

        List<String> optionsInMenu = Arrays.asList("Zoradiť abecedne", "Zoradiť podľa veku");
        for (int i = 0; i < 2; i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(optionsInMenu.get(i));
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
            item.setTitle(s);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Determining the sorting method by the selected item from options menu.
        switch (item.getItemId()) {
            case R.id.alphabetSorting:
                this.displayAthletes(this.club.getAthletesSortedByAlphabet());
                break;
            case R.id.ageSorting:
                this.displayAthletes(this.club.getAthletesSortedByAge());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.displayAthletes(this.club.getAthletesSortedByAlphabet());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_athletes, container, false);
    }
}