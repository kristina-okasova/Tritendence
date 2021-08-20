package com.example.tritendence.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Trainer;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class AttendanceData implements Serializable {
    private String sport;
    private Group group;
    private Trainer trainer;
    private ArrayList<Athlete> attendedAthletes;
    private LocalDate date;
    private LocalTime time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AttendanceData(Group group, String sport, Trainer trainer, ArrayList<Athlete> attendedAthletes, String data) {
        this.group = group;
        this.sport = sport;
        this.trainer = trainer;
        this.attendedAthletes = attendedAthletes;

        this.extractDateAndTime(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void extractDateAndTime(String data) {
        int year = Integer.parseInt(data.substring(0, 4));
        int month = Integer.parseInt(data.substring(4, 6));
        int day = Integer.parseInt(data.substring(6, 8));
        this.date = LocalDate.of(year, month, day);

        String timeData = data.substring(9, 14);
        this.time = LocalTime.parse(timeData);
    }

    public Group getGroup() {
        return this.group;
    }

    public String getDate() {
        return this.date.toString();
    }

    public String getTime() {
        return this.time.toString();
    }

    public String getSport() {
        return this.sport;
    }

    public ArrayList<Athlete> getAttendedAthletes() {
        return this.attendedAthletes;
    }

    public Trainer getTrainer() {
        return this.trainer;
    }

    public boolean containsAthlete(String selectedAthlete) {
        for (Athlete athlete : this.attendedAthletes) {
            if (athlete.getFullName().equals(selectedAthlete))
                return true;
        }
        return false;
    }
}
