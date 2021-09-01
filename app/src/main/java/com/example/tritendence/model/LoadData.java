package com.example.tritendence.model;

import android.app.Activity;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tritendence.activities.AddGroupActivity;
import com.example.tritendence.activities.HomeActivity;
import com.example.tritendence.activities.LogInActivity;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Admin;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class LoadData {
    private static final String GROUPS_CHILD_DATABASE = "Groups";
    private static final String ATHLETES_CHILD_DATABASE = "Athletes";
    private static final String TRAINERS_CHILD_DATABASE = "Trainers";
    private static final String ATTENDANCE_CHILD_DATABASE = "Attendance";
    private static final String ADMINS_CHILD_DATABASE = "Admin";

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
    private static final String ATHLETE_DAY_OF_BIRTH = "DayOfBirth";
    private static final String NUMBER_OF_TRAININGS = "NumberOfTrainings";

    private static final String ATTENDANCE_DATE = "Date";
    private static final String ATTENDANCE_TRAINER = "Trainer";
    private static final int NUMBER_OF_REQUIRED_DATA_FOR_ATTENDANCE = 4;
    private static final int NUMBER_OF_REQUIRED_DATA_FOR_TRAINER = 5;
    private static final int NUMBER_OF_REQUIRED_DATA_FOR_GROUP = 3;
    private static final int NUMBER_OF_REQUIRED_DATA_FOR_TRAINING_UNIT = 3;

    private TriathlonClub club;
    private transient final DatabaseReference database;
    private int numberOfGroups, numberOfAthletes, numberOfTrainers, numberOfFilledAttendances;

    public LoadData() {
        this.club = new TriathlonClub();
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
                loadAdminInformation(snapshot.child(ADMINS_CHILD_DATABASE));
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

        this.club.setNumberOfGroups(this.numberOfGroups);
        this.club.setNumberOfAthletes(this.numberOfAthletes);
        this.club.setNumberOfTrainers(this.numberOfTrainers);
        this.club.setNumberOfFilledAttendances(this.numberOfFilledAttendances);
        this.club.setNumberOfUsers(this.numberOfAthletes + this.numberOfTrainers);
    }

    private void loadGroupInformation(DataSnapshot snapshot) {
        for (int i = 1; i <= this.numberOfGroups; i++) {
            String groupID = String.valueOf(i);
            if (snapshot.child(groupID).getChildrenCount() != NUMBER_OF_REQUIRED_DATA_FOR_GROUP)
                continue;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadAthleteInformation(DataSnapshot snapshot) {
        for (int i = 1; i <= this.numberOfAthletes; i++) {
            String athleteID = String.valueOf(i);
            String name = Objects.requireNonNull(snapshot.child(athleteID).child(NAME).getValue()).toString();
            String surname = Objects.requireNonNull(snapshot.child(athleteID).child(SURNAME).getValue()).toString();
            String eMail = Objects.requireNonNull(snapshot.child(athleteID).child(EMAIL).getValue()).toString();
            String groupID = Objects.requireNonNull(snapshot.child(athleteID).child(ATHLETE_GROUP_ID).getValue()).toString();
            String numberOfTrainings = Objects.requireNonNull(snapshot.child(athleteID).child(NUMBER_OF_TRAININGS).getValue()).toString();
            String dayOfBirth = Objects.requireNonNull(snapshot.child(athleteID).child(ATHLETE_DAY_OF_BIRTH).getValue()).toString();

            Athlete athlete = new Athlete(i, name, surname, eMail, Integer.parseInt(groupID), Integer.parseInt(numberOfTrainings), dayOfBirth);
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

    private void loadAdminInformation(DataSnapshot snapshot) {
        String name = Objects.requireNonNull(snapshot.child(NAME).getValue()).toString();
        String surname = Objects.requireNonNull(snapshot.child(SURNAME).getValue()).toString();
        String email = Objects.requireNonNull(snapshot.child(EMAIL).getValue()).toString();
        int numberOfTrainings = Integer.parseInt(Objects.requireNonNull(snapshot.child(NUMBER_OF_TRAININGS).getValue()).toString());

        Admin admin = new Admin(0, name, surname, email, numberOfTrainings);
        this.club.setAdminOfClub(admin);
        this.club.addMember(admin);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadAttendanceInformation(DataSnapshot snapshot) {
        for (int i = 1; i <= this.numberOfFilledAttendances; i++) {
            String attendanceID = String.valueOf(i);
            String trainerID = ATTENDANCE_TRAINER + "1";
            int numberOfInsertedData = (int) snapshot.child(attendanceID).getChildrenCount();
            if (numberOfInsertedData < NUMBER_OF_REQUIRED_DATA_FOR_ATTENDANCE)
                continue;

            String date =Objects.requireNonNull(snapshot.child(attendanceID).child(ATTENDANCE_DATE).getValue()).toString();
            String sport = Objects.requireNonNull(snapshot.child(attendanceID).child(SPORT).getValue()).toString();
            String nameOfTrainer = Objects.requireNonNull(snapshot.child(attendanceID).child(trainerID).getValue()).toString();

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

            int numberOfExtraTrainers = ((int) snapshot.child(attendanceID).getChildrenCount()) - NUMBER_OF_REQUIRED_DATA_FOR_ATTENDANCE;
            for (int j = 0; j < numberOfExtraTrainers; j++) {
                trainerID = ATTENDANCE_TRAINER + String.valueOf(j + 2);
                nameOfTrainer = Objects.requireNonNull(snapshot.child(attendanceID).child(trainerID).getValue()).toString();
                trainer = (Trainer) this.findMember(nameOfTrainer);
                data.addTrainer(trainer);
            }

            this.club.addAttendanceData(data);
        }
    }

    private void createTrainingUnit(DataSnapshot snapshot, Group group, String sport, int ID, String groupID) {
        String unitID = String.valueOf(ID);
        if (snapshot.child(groupID).child(GROUPS_TIMETABLE).child(sport).child(unitID).getChildrenCount() != NUMBER_OF_REQUIRED_DATA_FOR_TRAINING_UNIT)
            return;
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
