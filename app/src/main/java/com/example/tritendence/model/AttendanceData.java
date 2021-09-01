package com.example.tritendence.model;

import android.icu.number.Scale;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tritendence.R;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Trainer;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AttendanceData implements Serializable {
    private String sport, note;
    private Group group;
    private ArrayList<Trainer> trainers;
    private ArrayList<Athlete> attendedAthletes;
    private LocalDate date;
    private LocalTime time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AttendanceData(Group group, String sport, Trainer trainer, ArrayList<Athlete> attendedAthletes, String data) {
        this.group = group;
        this.sport = sport;
        this.attendedAthletes = attendedAthletes;
        this.trainers = new ArrayList<>();
        this.trainers.add(trainer);

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

    public LocalDate getLocalDate() {
        return this.date;
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

    public Trainer getTrainer(int index) {
        return this.trainers.get(index);
    }

    public boolean containsAthlete(String selectedAthlete) {
        for (Athlete athlete : this.attendedAthletes) {
            if (athlete.getFullName().equals(selectedAthlete))
                return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDay() {
        return this.date.getDayOfWeek().toString();
    }

    public String getNote() {
        if (this.note == null || this.note.length() != 0)
            return this.note;
        return null;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNameOfAthleteAtIndex(int i) {
        return this.attendedAthletes.get(i).getFullName();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDayTranslation() {
        String day = this.date.getDayOfWeek().toString();
        switch (day) {
            case "MONDAY":
                return "Pondelok";
            case "TUESDAY":
                return "Utorok";
            case "WEDNESDAY":
                return "Streda";
            case "THURSDAY":
                return "Štvrtok";
            case "FRIDAY":
                return "Piatok";
            case "SATURDAY":
                return "Sobota";
            case "SUNDAY":
                return "Nedeľa";
            default:
                return null;
        }
    }

    public void addTrainer(Trainer trainer) {
        this.trainers.add(trainer);
    }

    public String getAllTrainers() {
        StringBuilder namesOfTrainersBuilder = new StringBuilder();
        for (int i = 0; i < this.trainers.size() - 1; i++)
            Objects.requireNonNull(namesOfTrainersBuilder).append(this.trainers.get(i).getFullName()).append("\n");
        String namesOfTrainers = Objects.requireNonNull(namesOfTrainersBuilder).toString();
        namesOfTrainers += this.trainers.get(this.trainers.size() - 1).getFullName();
        
        return namesOfTrainers;
    }

    public int getNumberOfTrainers() {
        return this.trainers.size();
    }
}
