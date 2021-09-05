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

import java.util.ArrayList;
import java.util.HashMap;

public class FilledAttendanceSheetFragment extends Fragment {
    private TextView trainingData, nameOfGroup, trainerOfGroup;
    private AutoCompleteTextView note;
    private ListScrollable attendanceSheet;
    private AttendanceData selectedAttendanceData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.selectedAttendanceData = (AttendanceData) requireActivity().getIntent().getExtras().getSerializable(getString(R.string.ATTENDANCE_DATA_EXTRA));

        this.trainerOfGroup = view.findViewById(R.id.filledAttendanceTrainer);
        this.nameOfGroup = view.findViewById(R.id.filledAttendanceGroupName);
        this.trainingData = view.findViewById(R.id.filledAttendanceTrainingData);
        this.note = view.findViewById(R.id.filledAttendanceNote);
        this.attendanceSheet = view.findViewById(R.id.filledAttendanceSheet);

        this.fillAttendanceInformation();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillAttendanceInformation() {
        this.trainingData.setText(String.format("%s\n%s - %s", this.selectedAttendanceData.getSport(), this.selectedAttendanceData.getDayTranslation(), this.selectedAttendanceData.getTime()));
        this.nameOfGroup.setText(this.selectedAttendanceData.getGroup().getName());
        this.trainerOfGroup.setText(this.selectedAttendanceData.getAllTrainers());
        this.note.setText(this.selectedAttendanceData.getNote());

        ArrayList<HashMap<String, Object>> dataForListOfAthletes = new ArrayList<>();
        for (int i = 0; i < this.selectedAttendanceData.getAttendedAthletes().size(); i++) {
            HashMap<String, Object> mappedData = new HashMap<>();
            mappedData.put(getString(R.string.NAME_OF_ATHLETE_ADAPTER), this.selectedAttendanceData.getNameOfAthleteAtIndex(i));

            dataForListOfAthletes.add(mappedData);
        }

        String[] insertingData = {getString(R.string.NAME_OF_ATHLETE_ADAPTER)};
        int[] UIData = {R.id.athletesAttendance};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataForListOfAthletes, R.layout.attendance_of_athlete, insertingData, UIData);
        this.attendanceSheet.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filled_attendance_sheet, container, false);
    }
}