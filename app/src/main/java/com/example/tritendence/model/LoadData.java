package com.example.tritendence.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class LoadData {
    private static final String GROUPS_CHILD_DATABASE = "Groups";
    private static final String ATHLETES_CHILD_DATABASE = "Athletes";
    private static final String TRAINERS_CHILD_DATABASE = "Trainers";
    private static final String ATTENDANCE_CHILD_DATABASE = "Attendance";

    private static final String NAME = "Name";
    private static final String SURNAME = "Surname";
    private static final String EMAIL = "E-Mail";
    private static final String SPORT = "Sport";

    private static final String GROUPS_CATEGORY = "Category";
    private static final String GROUPS_TIMETABLE = "Timetable";
    private static final String TIMETABLE_ATHLETICS = "Athletics";
    private static final String TIMETABLE_SWIMMING = "Swimming";
    private static final String TIMETABLE_CYCLING = "Cycling";

    private static final String UNIT_DAY = "day";
    private static final String UNIT_LOCATION = "location";
    private static final String UNIT_TIME = "time";

    private static final String ATHLETE_GROUP_ID = "GroupID";
    private static final String NUMBER_OF_TRAININGS = "NumberOfTrainings";

    private static final String ATTENDANCE_DATE = "Date";
    private static final String ATTENDANCE_TRAINER = "Trainer";
    private static final int NUMBER_OF_REQUIRED_DATA_FOR_ATTENDANCE = 4;
    private static final int NUMBER_OF_REQUIRED_DATA_FOR_TRAINER = 5;

    private TriathlonClub club;
    private final DatabaseReference database;
    private int numberOfGroups, numberOfAthletes, numberOfTrainers, numberOfFilledAttendances;

    public LoadData() {
        this.database = FirebaseDatabase.getInstance().getReference();
        this.loadData();
    }

    private void loadData() {
        this.database.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadAmounts(snapshot);
                loadAthleteInformation(snapshot.child(ATHLETES_CHILD_DATABASE));
                loadTrainerInformation(snapshot.child(TRAINERS_CHILD_DATABASE));
                loadGroupInformation(snapshot.child(GROUPS_CHILD_DATABASE));
                loadAttendanceInformation(snapshot.child(ATTENDANCE_CHILD_DATABASE));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadAmounts(DataSnapshot snapshot) {
        this.numberOfGroups = (int) snapshot.child(GROUPS_CHILD_DATABASE).getChildrenCount();
        this.numberOfAthletes = (int) snapshot.child(ATHLETES_CHILD_DATABASE).getChildrenCount();
        this.numberOfTrainers = (int) snapshot.child(TRAINERS_CHILD_DATABASE).getChildrenCount();
        this.numberOfFilledAttendances = (int) snapshot.child(ATTENDANCE_CHILD_DATABASE).getChildrenCount();
        this.club = new TriathlonClub(numberOfAthletes, numberOfTrainers, numberOfGroups, numberOfFilledAttendances);
    }

    private void loadGroupInformation(DataSnapshot snapshot) {
        for (int i = 1; i <= this.numberOfGroups; i++) {
            String groupID = String.valueOf(i);
            String nameOfGroup = Objects.requireNonNull(snapshot.child(groupID).child(NAME).getValue()).toString();
            String categoryOfGroup = Objects.requireNonNull(snapshot.child(groupID).child(GROUPS_CATEGORY).getValue()).toString();

            Group group = new Group(i, nameOfGroup, categoryOfGroup);

            int numberOfAthleticTrainingUnits = (int) snapshot.child(groupID).child(GROUPS_TIMETABLE).child(TIMETABLE_ATHLETICS).getChildrenCount();
            int numberOfSwimmingTrainingUnits = (int) snapshot.child(groupID).child(GROUPS_TIMETABLE).child(TIMETABLE_SWIMMING).getChildrenCount();
            int numberOfCyclingTrainingUnits = (int) snapshot.child(groupID).child(GROUPS_TIMETABLE).child(TIMETABLE_CYCLING).getChildrenCount();

            for (int j = 1; j <= numberOfAthleticTrainingUnits; j++)
                this.createTrainingUnit(snapshot, group, TIMETABLE_ATHLETICS, j, groupID);

            for (int j = 1; j <= numberOfSwimmingTrainingUnits; j++)
                this.createTrainingUnit(snapshot, group, TIMETABLE_SWIMMING, j, groupID);

            for (int j = 1; j <= numberOfCyclingTrainingUnits; j++)
                this.createTrainingUnit(snapshot, group, TIMETABLE_CYCLING, j, groupID);

            this.addAthletesToGroup(group);
            this.club.addGroup(group);
        }
    }

    private void loadAthleteInformation(DataSnapshot snapshot) {
        for (int i = 1; i <= this.numberOfAthletes; i++) {
            String athleteID = String.valueOf(i);
            String name = Objects.requireNonNull(snapshot.child(athleteID).child(NAME).getValue()).toString();
            String surname = Objects.requireNonNull(snapshot.child(athleteID).child(SURNAME).getValue()).toString();
            String eMail = Objects.requireNonNull(snapshot.child(athleteID).child(EMAIL).getValue()).toString();
            String groupID = Objects.requireNonNull(snapshot.child(athleteID).child(ATHLETE_GROUP_ID).getValue()).toString();

            Athlete athlete = new Athlete(i, name, surname, eMail, Integer.parseInt(groupID));
            this.club.addMember(athlete);
        }
    }

    private void loadTrainerInformation(DataSnapshot snapshot) {
        for (int i = 1; i <= this.numberOfTrainers; i++) {
            String trainerID = String.valueOf(i);
            int numberOfInsertedData = (int) snapshot.child(trainerID).getChildrenCount();
            if (numberOfInsertedData != NUMBER_OF_REQUIRED_DATA_FOR_TRAINER)
                continue;
            String name = Objects.requireNonNull(snapshot.child(trainerID).child(NAME).getValue()).toString();
            String surname = Objects.requireNonNull(snapshot.child(trainerID).child(SURNAME).getValue()).toString();
            String eMail = Objects.requireNonNull(snapshot.child(trainerID).child(EMAIL).getValue()).toString();
            String sport = Objects.requireNonNull(snapshot.child(trainerID).child(SPORT).getValue()).toString();
            int numberOfTrainings = Integer.parseInt(Objects.requireNonNull(snapshot.child(trainerID).child(NUMBER_OF_TRAININGS).getValue()).toString());

            Trainer trainer = new Trainer(i, name, surname, eMail, sport, numberOfTrainings);
            this.club.addMember(trainer);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadAttendanceInformation(DataSnapshot snapshot) {
        for (int i = 1; i <= this.numberOfFilledAttendances; i++) {
            String attendanceID = String.valueOf(i);
            int numberOfInsertedData = (int) snapshot.child(attendanceID).getChildrenCount();
            if (numberOfInsertedData != NUMBER_OF_REQUIRED_DATA_FOR_ATTENDANCE)
                continue;

            String date =Objects.requireNonNull(snapshot.child(attendanceID).child(ATTENDANCE_DATE).getValue()).toString();
            String sport = Objects.requireNonNull(snapshot.child(attendanceID).child(SPORT).getValue()).toString();
            String nameOfTrainer = Objects.requireNonNull(snapshot.child(attendanceID).child(ATTENDANCE_TRAINER).getValue()).toString();

            ArrayList<Athlete> attendedAthletes = new ArrayList<>();
            int numberOfAttendedAthletes = (int) snapshot.child(attendanceID).child(ATHLETES_CHILD_DATABASE).getChildrenCount();
            for (int j = 1; j <= numberOfAttendedAthletes; j++) {
                String athleteID = String.valueOf(j);
                String athleteName = Objects.requireNonNull(snapshot.child(attendanceID).child(ATHLETES_CHILD_DATABASE).child(athleteID).getValue()).toString();

                Athlete athlete = (Athlete) this.findMember(athleteName);
                attendedAthletes.add(athlete);
            }

            Trainer trainer = (Trainer) this.findMember(nameOfTrainer);
            int groupID = Integer.parseInt(String.valueOf(date.charAt(date.length() - 1)));
            Group group = this.findGroup(groupID);
            AttendanceData data = new AttendanceData(group, sport, trainer, attendedAthletes, date);
            this.club.addAttendanceData(data);
        }
    }

    private void createTrainingUnit(DataSnapshot snapshot, Group group, String sport, int ID, String groupID) {
        String unitID = String.valueOf(ID);
        TrainingUnit unit = new TrainingUnit();
        String day = Objects.requireNonNull(snapshot.child(groupID).child(GROUPS_TIMETABLE).child(sport).child(unitID).child(UNIT_DAY).getValue()).toString();
        String location = Objects.requireNonNull(snapshot.child(groupID).child(GROUPS_TIMETABLE).child(sport).child(unitID).child(UNIT_LOCATION).getValue()).toString();
        String time = Objects.requireNonNull(snapshot.child(groupID).child(GROUPS_TIMETABLE).child(sport).child(unitID).child(UNIT_TIME).getValue()).toString();

        unit.setDay(day);
        unit.setLocation(location);
        unit.setTime(time);
        unit.setSport(sport);

        group.addTrainingUnit(unit);
    }

    private void addAthletesToGroup(Group group) {
        for (int i = 0; i < this.club.getNumberOfUsers(); i++) {
            Member athlete = this.club.getMemberAtIndex(i);
            if (athlete instanceof Athlete) {
                if (((Athlete) athlete).getGroupID() == group.getID())
                    group.addAthlete((Athlete) athlete);
            }
        }
    }

    private Member findMember(String nameOfTrainer) {
        for (Member member : this.club.getMembersOfClub()) {
            if (member.getFullName().equals(nameOfTrainer))
                return member;
        }

        return null;
    }

    private Group findGroup(int groupID) {
        for (Group group : this.club.getGroupsOfClub()) {
            if (group.getID() == groupID)
                return group;
        }

        return null;
    }

    public TriathlonClub getClub() {
        return this.club;
    }
}