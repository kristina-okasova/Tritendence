package com.example.tritendence.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Admin;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private String signedTrainer;

    //Layout's items
    private ListView trainersTrainings;
    private TextView signedTrainerName, signedTrainerEmail, numberOfTrainings;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedTrainer = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));

        this.initializeLayoutItems(view);

        //Fill information about currently signed trainer.
        this.fillTrainerInformation();
    }

    private void initializeLayoutItems(View view) {
        this.signedTrainerName = view.findViewById(R.id.signedTrainerName);
        this.signedTrainerEmail = view.findViewById(R.id.signedTrainerEmail);
        this.numberOfTrainings = view.findViewById(R.id.numberOfTrainingUnits);
        this.trainersTrainings = view.findViewById(R.id.trainersTrainings);
    }

    private void fillTrainerInformation() {
        ArrayList<HashMap<String, Object>> dataForListOfTrainings = new ArrayList<>();

        //Creating hashmap consisting of the attendance data of signed trainer for adapter.
        for (AttendanceData attendance : this.club.getAttendanceData()) {
            HashMap<String, Object> mappedData = new HashMap<>();

            //Looking for signed trainer in list of trainers of concrete attendance data.
            for (Trainer trainer : attendance.getTrainers()) {
                if (trainer.getFullName().equals(signedTrainer)) {
                    mappedData.put(getString(R.string.TRAINING_OF_TRAINER_ADAPTER), attendance.getDate() + " " + attendance.getTime() + "\n" + attendance.getGroup().getName());
                    dataForListOfTrainings.add(mappedData);
                }
            }
        }

        //Looking for the signed user by his name.
        for (Member member : this.club.getMembersOfClub()) {
            if ((member instanceof Trainer || member instanceof Admin) && member.getFullName().equals(this.signedTrainer)) {
                this.signedTrainerName.setText(member.getFullName());

                //Fill in data if the signed user is trainer.
                if (member instanceof Trainer) {
                    this.signedTrainerEmail.setText(((Trainer) member).getEmail());
                    this.numberOfTrainings.setText(String.valueOf(((Trainer) member).getNumberOfTrainings()));
                }

                //Fill in data if the signed user is admin.
                if (member instanceof Admin) {
                    this.signedTrainerEmail.setText(((Admin) member).getEmail());
                    this.numberOfTrainings.setText(String.valueOf(((Admin) member).getNumberOfTrainings()));
                }

                //Linking keys of hashmap to items of the layout used by adapter.
                String[] insertingData = {getString(R.string.TRAINING_OF_TRAINER_ADAPTER)};
                int[] UIData = {R.id.trainersTrainingData};

                //Creating adapter and setting it to the list of attendances.
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfTrainings, R.layout.training_in_list_of_trainers_trainings, insertingData, UIData);
                this.trainersTrainings.setAdapter(adapter);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
        this.fillTrainerInformation();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
