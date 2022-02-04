package com.example.tritendence.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AthleteInformationFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String selectedAthlete, signedUser, sportSelection;

    //Layout's items
    private TextView nameOfAthlete, numberOfTrainings, dayOfBirth, group;

    public AthleteInformationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedUser = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.sportSelection = requireActivity().getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));
        this.loadData = (LoadData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.selectedAthlete =  requireActivity().getIntent().getExtras().getString(getString(R.string.ATHLETE_NAME_EXTRA));
        this.initializeLayoutItems(view);
        this.loadData.setActivity(getActivity());

        //Finding type of user and displaying accurate layout items and information about selected athlete.
        this.findTypeOfUser(view);
        int numberOfTrainings = this.fillInAthletesAttendance(view);
        this.fillInAthleteInformation(numberOfTrainings);
    }

    private void initializeLayoutItems(View view) {
        this.nameOfAthlete = view.findViewById(R.id.nameOfAthlete);
        this.dayOfBirth = view.findViewById(R.id.athletesDayOfBirth);
        this.numberOfTrainings = view.findViewById(R.id.numberOfAthletesTrainings);
        this.group = view.findViewById(R.id.athletesGroup);
    }

    private void findTypeOfUser(View view) {
        //If signed user is admin then show imageview to edit and delete selected athlete.
        if (this.club.getAdminOfClub().getFullName().equals(this.signedUser)) {
            ImageView deleteAthleteIcon = view.findViewById(R.id.deleteAthleteIcon);
            ImageView editAthleteIcon = view.findViewById(R.id.editAthleteIcon);

            deleteAthleteIcon.setVisibility(View.VISIBLE);
            editAthleteIcon.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fillInAthleteInformation(int numberOfTrainings) {
        //Finding athlete in the club by name and filling its information.
        for (Member athlete : this.club.getMembersOfClub()) {
            if (athlete instanceof Athlete && athlete.getFullName().equals(selectedAthlete)) {
                this.nameOfAthlete.setText(athlete.getFullName());
                this.dayOfBirth.setText(((Athlete) athlete).getDayOfBirth());
                this.numberOfTrainings.setText(String.valueOf(numberOfTrainings));
                this.group.setText(this.findGroupByID(((Athlete) athlete).getGroupID()));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int fillInAthletesAttendance(View view) {
        ArrayList<HashMap<String, Object>> dataForListOfAthletes = new ArrayList<>();
        //Creating hashmap consisting of the attendance's data for adapter.
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            if (attendanceData.containsAthlete(this.selectedAthlete)) {
                HashMap<String, Object> mappedData = new HashMap<>();

                mappedData.put(getString(R.string.TRAINING_DATA), attendanceData.getFormatDate() + " " + attendanceData.getTime() + "\n" + attendanceData.getSport());
                dataForListOfAthletes.add(mappedData);
            }
        }

        //Linking keys of hashmap to items of the layout used by adapter.
        String[] insertingData = {getString(R.string.TRAINING_DATA)};
        int[] UIData = {R.id.athletesAttendance};

        //Creating adapter and setting it to the list of attendance data.
        ListView athletesAttendance = view.findViewById(R.id.attendanceOfAthleteInformation);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfAthletes, R.layout.attendance_of_athlete, insertingData, UIData);
        athletesAttendance.setAdapter(adapter);

        return dataForListOfAthletes.size();
    }

    private String findGroupByID(int groupID) {
        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getID() == groupID)
                return group.getName();
        }
        return getString(R.string.GROUP_NOT_ASSIGNED);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteAthlete() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        //Deleting athlete by setting its group ID to -1.
        Athlete athleteToDelete = this.findAthleteByName();
        root.child(getString(R.string.ATHLETES_CHILD_DB) + "/" + Objects.requireNonNull(athleteToDelete).getID()).child(getString(R.string.GROUP_ID_DB)).setValue(-1);

        //Deleting athlete the group.
        for (Group group : this.club.getGroupsOfClub()) {
            for (Athlete athlete : group.getAthletesOfGroup()) {
                if (athlete.getFullName().equals(athleteToDelete.getFullName()))
                    group.deleteAthleteFromGroup(athlete);
            }
        }
        this.loadHomePage();
    }

    public Athlete findAthleteByName() {
        for (Member member : this.club.getMembersOfClub()) {
            if (member instanceof Athlete && member.getFullName().equals(this.selectedAthlete))
                return (Athlete) member;
        }
        return null;
    }

    private void loadHomePage() {
        //Creating intent of Home Activity to return to home page after deleting an athlete.
        Intent athleteInformationPage = new Intent(this.getContext(), HomeActivity.class);
        athleteInformationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        athleteInformationPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), this.signedUser);
        athleteInformationPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        athleteInformationPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        athleteInformationPage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), R.id.athletesFragment);
        startActivity(athleteInformationPage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_athlete_information, container, false);
    }
}