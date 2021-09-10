package com.example.tritendence.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Admin;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class TriathlonClub implements Serializable {
    private ArrayList<Member> membersOfClub;
    private ArrayList<Group> groupsOfClub;
    private ArrayList<AttendanceData> attendanceData;
    private int numberOfUsers, numberOfGroups, numberOfAthletes, numberOfTrainers, numberOfFilledAttendances, numberOfWeek;
    private Admin adminOfClub;

    public TriathlonClub() {
        this.membersOfClub = new ArrayList<>();
        this.groupsOfClub = new ArrayList<>();
        this.attendanceData = new ArrayList<>();
    }

    public void updateNumberOfTrainers() {
        this.numberOfTrainers++;
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

    public Group getGroupAtIndex(int i) {
        return this.groupsOfClub.get(i);
    }

    public Member getMemberAtIndex(int i) {
        return this.membersOfClub.get(i);
    }

    public ArrayList<AttendanceData> getAttendanceData() {
        return this.attendanceData;
    }

    public int getNumberOfTrainers() {
        return this.numberOfTrainers;
    }

    public ArrayList<String> getNamesOfTrainers(ArrayList<String> trainersOfClub) {
        String signedTrainer = trainersOfClub.get(0);
        for (Member member : this.membersOfClub) {
            if (member instanceof Trainer && !member.getFullName().equals(signedTrainer))
                trainersOfClub.add(member.getFullName());
        }
        return trainersOfClub;
    }

    public int getNumberOfAthletes() {
        return this.numberOfAthletes;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Member> getAthletesSortedByAlphabet() {
        ArrayList<Member> sortedMembers = new ArrayList<>(this.membersOfClub);
        for (Member member : this.membersOfClub) {
            if (!(member instanceof Athlete))
                sortedMembers.remove(member);
        }

        Collator collator = Collator.getInstance(new Locale("sk_SK"));
        sortedMembers.sort((member1, member2) -> {
            if (collator.compare(member1.getSurname(), member2.getSurname()) == 0)
                return collator.compare(member1.getName(), member2.getName());
            else
                return collator.compare(member1.getSurname(), member2.getSurname());
        });
        return sortedMembers;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Member> getAthletesSortedByAge() {
        ArrayList<Member> sortedMembers = new ArrayList<>(this.membersOfClub);
        for (Member member : this.membersOfClub) {
            if (!(member instanceof Athlete))
                sortedMembers.remove(member);
        }

        Collator collator = Collator.getInstance(new Locale("sk_SK"));
        Collections.sort(sortedMembers, (member1, member2) -> {
            if (((Athlete) member1).getDate().compareTo(((Athlete) member2).getDate()) == 0)
                return collator.compare(member1.getSurname(), member2.getSurname());
            else
                return ((Athlete) member1).getDate().compareTo(((Athlete) member2).getDate());
        });
        return sortedMembers;
    }

    public void increaseNumberOfGroups() {
        this.numberOfGroups++;
    }

    public void setNumberOfGroups(int numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
    }

    public void setNumberOfAthletes(int numberOfAthletes) {
        this.numberOfAthletes = numberOfAthletes;
    }

    public void setNumberOfTrainers(int numberOfTrainers) {
        this.numberOfTrainers = numberOfTrainers;
    }

    public void setNumberOfFilledAttendances(int numberOfFilledAttendances) {
        this.numberOfFilledAttendances = numberOfFilledAttendances;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public void setAdminOfClub(Admin adminOfClub) {
        this.adminOfClub = adminOfClub;
    }

    public Admin getAdminOfClub() {
        return this.adminOfClub;
    }

    public ArrayList<Member> getTrainersSortedByAlphabet() {
        ArrayList<Member> sortedMembers = new ArrayList<>(this.membersOfClub);
        for (Member member : this.membersOfClub) {
            if (!(member instanceof Trainer))
                sortedMembers.remove(member);
        }

        Collections.sort(sortedMembers, (member1, member2) -> member1.getSurname().compareTo(member2.getSurname()));
        return sortedMembers;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Member> getTrainersSortedByAge() {
        ArrayList<Member> sortedMembers = new ArrayList<>(this.membersOfClub);
        for (Member member : this.membersOfClub) {
            if (!(member instanceof Trainer))
                sortedMembers.remove(member);
        }

        Collections.sort(sortedMembers, (member1, member2) -> ((Athlete) member1).getDate().compareTo(((Athlete) member2).getDate()));
        return sortedMembers;
    }

    public void setNumberOfWeek(int numberOfWeek) {
        this.numberOfWeek = numberOfWeek;
    }

    public int getNumberOfWeek() {
        return this.numberOfWeek;
    }

    public void clearData() {
        this.groupsOfClub = new ArrayList<>();
        this.membersOfClub = new ArrayList<>();
        this.attendanceData = new ArrayList<>();
    }
}
