package com.example.tritendence.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private ListView trainersTrainings;
    private TriathlonClub club;
    private TextView signedTrainerName, signedTrainerEmail, numberOfTrainings;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        this.signedTrainerName = view.findViewById(R.id.signedTrainerName);
        this.signedTrainerEmail = view.findViewById(R.id.signedTrainerEmail);
        this.numberOfTrainings = view.findViewById(R.id.numberOfTrainingUnits);
        this.trainersTrainings = view.findViewById(R.id.trainersTrainings);

        this.fillTrainerInformation();
    }

    private void fillTrainerInformation() {
        String signedTrainer = requireActivity().getIntent().getExtras().getString("SIGNED_USER");
        ArrayList<HashMap<String, Object>> dataForListOfTrainings = new ArrayList<>();

        for (AttendanceData attendance : this.club.getAttendanceData()) {
            HashMap<String, Object> mappedData = new HashMap<>();
            if (attendance.getTrainer().getFullName().equals(signedTrainer))
                mappedData.put("trainingOfTrainer", attendance.getDate() + " " + attendance.getTime() + "\n" + attendance.getGroup().getName());

            dataForListOfTrainings.add(mappedData);
        }

        for (Member member : this.club.getMembersOfClub()) {
            if (member instanceof Trainer) {
                if (member.getFullName().equals(signedTrainer)) {
                    this.signedTrainerName.setText(member.getFullName());
                    this.signedTrainerEmail.setText(member.getEmail());
                    this.numberOfTrainings.setText(String.valueOf(((Trainer) member).getNumberOfTrainings()));

                    String[] insertingData = {"trainingOfTrainer"};
                    int[] UIData = {R.id.trainersTrainingData};
                    SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfTrainings, R.layout.training_in_list_of_trainers_trainings, insertingData, UIData);
                    this.trainersTrainings.setAdapter(adapter);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
