package com.example.tritendence.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.activities.AttendanceSheetActivity;

public class AttendanceSheetFragment extends Fragment {

    private ListView attendanceSheet;
    private Button confirmation;
    private ArrayAdapter<String> adapter;
    private String[] membersOfGroup = {"Tomáš Drobný", "Jakub Hunoc", "Oliver Trepnek", "Barbora Nitková", "Gabriela Hrepková", "Veronika Drepkovecová"};

    public AttendanceSheetFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.attendanceSheet = view.findViewById(R.id.attendanceSheet);
        this.adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, this.membersOfGroup);
        this.attendanceSheet.setAdapter(this.adapter);

        this.confirmation = view.findViewById(R.id.attendanceConfirmationButton);
        this.confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String members = "Members:\n";

                for (int i = 0; i < attendanceSheet.getCount(); i++) {
                    if (attendanceSheet.isItemChecked(i)) {
                        members += attendanceSheet.getItemAtPosition(i).toString() + "\n";
                    }
                }

                Toast.makeText(getActivity(), members, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
    }
}