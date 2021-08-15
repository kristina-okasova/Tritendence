package com.example.tritendence.model;

import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Member;

import java.io.Serializable;
import java.util.ArrayList;

public class TriathlonClub implements Serializable {
    private ArrayList<Member> membersOfClub;
    private ArrayList<Group> groupsOfClub;
    private ArrayList<AttendanceData> attendanceData;
    private int numberOfUsers, numberOfGroups, numberOfAthletes, numberOfTrainers, numberOfFilledAttendances;

    public TriathlonClub(int numberOfAthletes, int numberOfTrainers, int numberOfGroups, int numberOfFilledAttendances) {
        this.numberOfAthletes = numberOfAthletes;
        this.numberOfTrainers = numberOfTrainers;
        this.numberOfGroups = numberOfGroups;
        this.numberOfFilledAttendances = numberOfFilledAttendances;
        this.numberOfUsers = numberOfAthletes + numberOfTrainers;

        this.membersOfClub = new ArrayList<>();
        this.groupsOfClub = new ArrayList<>();
        this.attendanceData = new ArrayList<>();
    }

    public void updateNumberOfUsers() {
        this.numberOfUsers++;
    }

    public int getNumberOfUsers() {
        return this.numberOfUsers;
    }

    public void addMember(Member member) {
        this.membersOfClub.add(member);
    }

    public ArrayList<Group> getGroupsOfClub() {
        return this.groupsOfClub;
    }

    public void addGroup(Group group) {
        this.groupsOfClub.add(group);
    }

    public ArrayList<Member> getMembersOfClub() {
        return this.membersOfClub;
    }

    public void addAttendanceData(AttendanceData data) {
        this.attendanceData.add(data);
    }

    public int getNumberOfFilledAttendances() {
        return this.numberOfFilledAttendances;
    }

    public int getNumberOfGroups() {
        return this.numberOfGroups;
    }

    public String getGroupAtIndex(int i) {
        return this.groupsOfClub.get(i).getName();
    }

    public Member getMemberAtIndex(int i) {
        return this.membersOfClub.get(i);
    }

    public ArrayList<AttendanceData> getAttendanceData() {
        return this.attendanceData;
    }
}
