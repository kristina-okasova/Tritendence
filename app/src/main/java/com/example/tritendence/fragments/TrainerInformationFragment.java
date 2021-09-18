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
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainerInformationFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private String selectedTrainer;

    //Layout's items
    private TextView nameOfTrainer, email, numberOfTrainings;
    private ListView trainersAttendance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.selectedTrainer = requireActivity().getIntent().getExtras().getString(getString(R.string.TRAINER_NAME_EXTRA));
        this.initializeLayoutItems(view);

        //Filling all the required information about the trainer.
        this.initializeTrainersAttendance(view);
        this.fillInTrainersInformation();
    }

    private void initializeLayoutItems(View view) {
        this.nameOfTrainer = view.findViewById(R.id.nameOfTrainer);
        this.email = view.findViewById(R.id.trainersEmail);
        this.numberOfTrainings = view.findViewById(R.id.numberOfTrainersTrainings);
        this.trainersAttendance = view.findViewById(R.id.attendanceOfTrainerInformation);
    }

    private void initializeTrainersAttendance(View view) {
        ArrayList<HashMap<String, Object>> dataForListOfTrainings = new ArrayList<>();
        //Creating hashmap consisting of the trainer's attendance data.
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            for (Member trainer : attendanceData.getTrainers()) {
                if (trainer.getFullName().equals(this.selectedTrainer)) {
                    HashMap<String, Object> mappedData = new HashMap<>();

                    mappedData.put(getString(R.string.TRAINING_DATA_ADAPTER), attendanceData.getDate() + " " + attendanceData.getTime() + "\n" + attendanceData.getSport());
                    dataForListOfTrainings.add(mappedData);
                }
            }
        }

        //Linking keys of hashmap to items of the layout used by adapter.
        String[] insertingData = {getString(R.string.TRAINING_DATA_ADAPTER)};
        int[] UIData = {R.id.athletesAttendance};

        //Creating adapter and setting it to the list of attendance data.
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfTrainings, R.layout.attendance_of_athlete, insertingData, UIData);
        this.trainersAttendance.setAdapter(adapter);
    }

    private void fillInTrainersInformation() {
        //Finding signed trainer in the club by its name and filling its data.
        for (Member member : this.club.getMembersOfClub()) {
            if (member instanceof Trainer && member.getFullName().equals(this.selectedTrainer)) {
                this.nameOfTrainer.setText(member.getFullName());
                this.email.setText(((Trainer) member).getEmail());
                this.numberOfTrainings.setText(String.valueOf(((Trainer) member).getNumberOfTrainings()));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trainer_information, container, false);
    }
}