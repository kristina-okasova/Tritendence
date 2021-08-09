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
import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AttendanceSheetFragment extends Fragment {
    private final static String ATHLETES_CHILD_DATABASE = "Athletes";
    private final static String ATHLETES_NAME = "name";
    private final static String ATHLETES_SURNAME = "surname";
    private final static String ATHLETES_EMAIL = "email";
    private final static String ATHLETES_GROUP_ID = "groupID";

    private ListView attendanceSheet;
    private DatabaseReference database;
    private Button confirmation;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> membersOfGroup;
    private boolean dataLoaded = false;

    public AttendanceSheetFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initializeMembersOfGroup(view);

        this.confirmation = view.findViewById(R.id.attendanceConfirmationButton);
        this.confirmation.setOnClickListener(v -> {
            String members = "Members:\n";

            for (int i = 0; i < attendanceSheet.getCount(); i++) {
                if (attendanceSheet.isItemChecked(i)) {
                    members += attendanceSheet.getItemAtPosition(i).toString() + "\n";
                }
            }

            Toast.makeText(getActivity(), members, Toast.LENGTH_LONG).show();
        });
    }

    private void initializeMembersOfGroup(View view) {
        membersOfGroup = new ArrayList<>();
        this.database = FirebaseDatabase.getInstance().getReference().child(ATHLETES_CHILD_DATABASE);

        this.database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfAthletes = (int) snapshot.getChildrenCount();

                for (int i = 1; i <= numberOfAthletes; i++) {
                    String athleteID = String.valueOf(i);
                    Athlete athlete = new Athlete();
                    athlete.setID(i);
                    athlete.setName(snapshot.child(athleteID).child(ATHLETES_NAME).getValue().toString());
                    athlete.setSurname(snapshot.child(athleteID).child(ATHLETES_SURNAME).getValue().toString());
                    athlete.setEmail(snapshot.child(athleteID).child(ATHLETES_EMAIL).getValue().toString());
                    String athleteGroupID = snapshot.child(athleteID).child(ATHLETES_GROUP_ID).getValue().toString();
                    athlete.setGroupID(Integer.parseInt(athleteGroupID));

                    String groupID = getActivity().getIntent().getExtras().getString("GROUP_ID");

                    if (athlete.getGroupID() == Integer.parseInt(groupID))
                        membersOfGroup.add(athlete.getFullName());
                }

                System.out.println("Members in function: " + membersOfGroup);
                notifyAttendanceSheet(view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void notifyAttendanceSheet(View view) {
        this.attendanceSheet = view.findViewById(R.id.attendanceSheet);
        this.adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, this.membersOfGroup);
        this.attendanceSheet.setAdapter(this.adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_attendance_sheet, container, false);
    }
}