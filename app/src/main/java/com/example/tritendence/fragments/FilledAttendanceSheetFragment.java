package com.example.tritendence.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.tritendence.R;
import com.example.tritendence.model.AttendanceData;
import com.example.tritendence.model.lists.ListScrollable;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class FilledAttendanceSheetFragment extends Fragment {
    //Intent's extras
    private AttendanceData selectedAttendanceData;

    private TextView trainingData, nameOfGroup, trainerOfGroup, numberOfAttendedAthletes;
    private AutoCompleteTextView note;
    private ListScrollable attendanceSheet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting extras of the intent.
        this.selectedAttendanceData = (AttendanceData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.ATTENDANCE_DATA_EXTRA));
        this.initializeLayoutItems(view);

        this.fillAttendanceInformation();
    }

    private void initializeLayoutItems(View view) {
        this.trainerOfGroup = view.findViewById(R.id.filledAttendanceTrainer);
        this.nameOfGroup = view.findViewById(R.id.filledAttendanceGroupName);
        this.numberOfAttendedAthletes = view.findViewById(R.id.filledAttendanceNumberOfAthletes);
        this.trainingData = view.findViewById(R.id.filledAttendanceTrainingData);
        this.attendanceSheet = view.findViewById(R.id.filledAttendanceSheet);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillAttendanceInformation() {
        //Filling data of the filled attendance sheet.
        this.trainingData.setText(String.format("%s\n%s, %s, %s", this.selectedAttendanceData.getSport(), this.selectedAttendanceData.getFormatDate(), this.selectedAttendanceData.getDayTranslation(), this.selectedAttendanceData.getTime()));
        this.nameOfGroup.setText(this.selectedAttendanceData.getGroup().getName());
        this.trainerOfGroup.setText(this.selectedAttendanceData.getAllTrainers());
        this.numberOfAttendedAthletes.setText(String.valueOf(this.selectedAttendanceData.getAttendedAthletes().size()));

        //Creating hashmap consisting of the attended athlete's name.
        ArrayList<HashMap<String, Object>> dataForListOfAthletes = new ArrayList<>();
        for (int i = 0; i < this.selectedAttendanceData.getAttendedAthletes().size(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();
            mappedData.put(getString(R.string.NAME_OF_ATHLETE_ADAPTER), this.selectedAttendanceData.getNameOfAthleteAtIndex(i));

            dataForListOfAthletes.add(mappedData);
        }

        //Linking keys of hashmap to items of the layout used by adapter.
        String[] insertingData = {getString(R.string.NAME_OF_ATHLETE_ADAPTER)};
        int[] UIData = {R.id.athletesAttendance};

        //Creating adapter and setting it to the list of attended athletes.
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfAthletes, R.layout.attendance_of_athlete, insertingData, UIData);
        this.attendanceSheet.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filled_attendance_sheet, container, false);
    }
}