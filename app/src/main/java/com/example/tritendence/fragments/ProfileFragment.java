package com.example.tritendence.fragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tritendence.R;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Admin;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    //Intent's extras
    private TriathlonClub club;
    private String signedTrainer, theme;

    //Layout's items
    private ListView trainersTrainings;
    private TextView signedTrainerName, signedTrainerEmail, numberOfTrainings;
    private Spinner themeOfUser;

    private HomeActivity activity;

    public ProfileFragment(HomeActivity activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.club = (TriathlonClub) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.signedTrainer = requireActivity().getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.theme = requireActivity().getIntent().getExtras().getString(getString(R.string.THEME_EXTRA));

        this.initializeLayoutItems(view);
        this.setThemesSpinner();

        //Fill information about currently signed trainer.
        this.fillTrainerInformation();

        //Setting listener of themes spinner.
        this.themeOfUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (themeOfUser.getSelectedItemId() == 0)
                    return;
                String selectedTheme = themeOfUser.getSelectedItem().toString();
                Member currentTrainer = null;
                //Finding currently signed user by its name.
                for (Member trainer : club.getTrainersSortedByAlphabet()) {
                    if (trainer instanceof Trainer && trainer.getFullName().equals(signedTrainer))
                        currentTrainer = trainer;
                }
                if (club.getAdminOfClub().getFullName().equals(signedTrainer))
                    currentTrainer = club.getAdminOfClub();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                //Setting new theme to the signed user.
                if (currentTrainer instanceof Trainer) {
                    if (((Trainer) currentTrainer).getTheme().equals(selectedTheme))
                        return;

                    database.child(getString(R.string.TRAINERS_DB) + "/" + ((Trainer) currentTrainer).getIDText() + "/" + (getString(R.string.THEME_DB))).setValue(selectedTheme);
                    Objects.requireNonNull((Trainer) currentTrainer).setTheme(selectedTheme);
                }

                if (currentTrainer instanceof Admin) {
                    if (((Admin) currentTrainer).getTheme().equals(selectedTheme))
                        return;

                    database.child(getString(R.string.ADMIN_DB) + "/" + (getString(R.string.THEME_DB))).setValue(selectedTheme);
                    Objects.requireNonNull((Admin) currentTrainer).setTheme(selectedTheme);
                }

                activity.changeTheme(selectedTheme);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initializeLayoutItems(View view) {
        this.signedTrainerName = view.findViewById(R.id.signedTrainerName);
        this.signedTrainerEmail = view.findViewById(R.id.signedTrainerEmail);
        this.numberOfTrainings = view.findViewById(R.id.numberOfTrainingUnits);
        this.trainersTrainings = view.findViewById(R.id.trainersTrainings);
        this.themeOfUser = view.findViewById(R.id.themeOfUser);
    }

    private void setThemesSpinner() {
        ArrayList<String> listOfThemes = new ArrayList<>();
        switch (this.theme) {
            case "DarkRed":
                listOfThemes.add("DarkRed");
                listOfThemes.add("DarkBlue");
                listOfThemes.add("LightRed");
                listOfThemes.add("LightBlue");
                break;
            case "DarkBlue":
                listOfThemes.add("DarkBlue");
                listOfThemes.add("DarkRed");
                listOfThemes.add("LightRed");
                listOfThemes.add("LightBlue");
                break;
            case "LightRed":
                listOfThemes.add("LightRed");
                listOfThemes.add("DarkBlue");
                listOfThemes.add("DarkRed");
                listOfThemes.add("LightBlue");
                break;
            case "LightBlue":
                listOfThemes.add("LightBlue");
                listOfThemes.add("DarkBlue");
                listOfThemes.add("LightRed");
                listOfThemes.add("DarkRed");
                break;
        }

        //Setting adapter for themes available in app.
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, listOfThemes);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.themeOfUser.setAdapter(adapterSpinner);

        //Setting default value of theme to the selected theme of currently signed user.
        this.themeOfUser.setSelection(0);
    }

    private void fillTrainerInformation() {
        ArrayList<HashMap<String, Object>> dataForListOfTrainings = new ArrayList<>();

        //Creating hashmap consisting of the attendance data of signed trainer for adapter.
        for (AttendanceData attendance : this.club.getAttendanceData()) {
            HashMap<String, Object> mappedData = new HashMap<>();

            //Looking for signed trainer in list of trainers of concrete attendance data.
            for (Member trainer : attendance.getTrainers()) {
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
