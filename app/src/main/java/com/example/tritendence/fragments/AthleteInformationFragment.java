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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AttendanceData;
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
    private static final String ATHLETES_CHILD_DATABASE = "Athletes";

    private TextView nameOfAthlete, numberOfTrainings, dayOfBirth, group;
    private TriathlonClub club;
    private String selectedAthlete;

    public AthleteInformationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.selectedAthlete =  requireActivity().getIntent().getExtras().getString("ATHLETE_NAME");

        this.nameOfAthlete = view.findViewById(R.id.nameOfAthlete);
        this.dayOfBirth = view.findViewById(R.id.athletesDayOfBirth);
        this.numberOfTrainings = view.findViewById(R.id.numberOfAthletesTrainings);
        this.group = view.findViewById(R.id.athletesGroup);

        ArrayList<HashMap<String, Object>> dataForListOfAthletes = new ArrayList<>();
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            if (attendanceData.containsAthlete(this.selectedAthlete)) {
                HashMap<String, Object> mappedData = new HashMap<>();

                mappedData.put("trainingData", attendanceData.getDate() + " " + attendanceData.getTime() + "\n" + attendanceData.getSport());
                dataForListOfAthletes.add(mappedData);
            }
        }

        String[] insertingData = {"trainingData"};
        int[] UIData = {R.id.athletesAttendance};

        ListView athletesAttendance = view.findViewById(R.id.attendanceOfAthleteInformation);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfAthletes, R.layout.attendance_of_athlete, insertingData, UIData);
        athletesAttendance.setAdapter(adapter);

        this.fillInAthleteInformation();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fillInAthleteInformation() {
        for (Member athlete : this.club.getMembersOfClub()) {
            if (athlete instanceof Athlete && athlete.getFullName().equals(selectedAthlete)) {
                this.nameOfAthlete.setText(athlete.getFullName());
                this.dayOfBirth.setText(((Athlete) athlete).getDayOfBirth());
                this.numberOfTrainings.setText(String.valueOf(((Athlete) athlete).getNumberOfTrainings()));
                this.group.setText(this.findGroupByID(((Athlete) athlete).getGroupID()));
            }
        }
    }

    private String findGroupByID(int groupID) {
        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getID() == groupID)
                return group.getName();
        }
        return "--nezaradený--";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_athlete_information, container, false);
    }

    public void deleteAthlete() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        Athlete athleteToDelete = this.findAthleteByName(this.selectedAthlete);
        root.child(ATHLETES_CHILD_DATABASE + "/" + Objects.requireNonNull(athleteToDelete).getID()).removeValue();

        String signedUser = requireActivity().getIntent().getExtras().getString("SIGNED_USER");
        Intent athleteInformationPage = new Intent(this.getContext(), HomeActivity.class);
        athleteInformationPage.putExtra("TRIATHLON_CLUB", this.club);
        athleteInformationPage.putExtra("SIGNED_USER", signedUser);
        athleteInformationPage.putExtra("SELECTED_FRAGMENT", R.id.athletesFragment);
        startActivity(athleteInformationPage);
    }

    private Athlete findAthleteByName(String selectedAthlete) {
        for (Member member : this.club.getMembersOfClub()) {
            if (member instanceof Athlete && member.getFullName().equals(selectedAthlete))
                return (Athlete) member;
        }
        return null;
    }

    public Athlete findSelectedAthlete() {
        return this.findAthleteByName(this.selectedAthlete);
    }
}