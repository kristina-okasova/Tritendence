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
    private TriathlonClub club;
    private TextView nameOfTrainer, email, numberOfTrainings;
    private String selectedTrainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.selectedTrainer = requireActivity().getIntent().getExtras().getString("TRAINER_NAME");
        this.nameOfTrainer = view.findViewById(R.id.nameOfTrainer);
        this.email = view.findViewById(R.id.trainersEmail);
        this.numberOfTrainings = view.findViewById(R.id.numberOfTrainersTrainings);

        ArrayList<HashMap<String, Object>> dataForListOfTrainings = new ArrayList<>();
        for (AttendanceData attendanceData : this.club.getAttendanceData()) {
            if (attendanceData.getTrainer(0).getFullName().equals(this.selectedTrainer)) {
                HashMap<String, Object> mappedData = new HashMap<>();

                mappedData.put("trainingData", attendanceData.getDate() + " " + attendanceData.getTime() + "\n" + attendanceData.getSport());
                dataForListOfTrainings.add(mappedData);
            }
        }

        String[] insertingData = {"trainingData"};
        int[] UIData = {R.id.athletesAttendance};

        ListView trainersAttendance = view.findViewById(R.id.attendanceOfTrainerInformation);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfTrainings, R.layout.attendance_of_athlete, insertingData, UIData);
        trainersAttendance.setAdapter(adapter);

        this.fillInTrainersInformation();
    }

    private void fillInTrainersInformation() {
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