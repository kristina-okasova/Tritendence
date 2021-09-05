package com.example.tritendence.model;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentContainerView;

import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.example.tritendence.model.users.Trainer;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

public class AttendanceData implements Serializable {
    private static final String MONDAY_DB = "MONDAY";
    private static final String TUESDAY_DB = "TUESDAY";
    private static final String WEDNESDAY_DB = "WEDNESDAY";
    private static final String THURSDAY_DB = "THURSDAY";
    private static final String FRIDAY_DB = "FRIDAY";
    private static final String SATURDAY_DB = "SATURDAY";
    private static final String SUNDAY_DB = "SUNDAY";

    private static final String MONDAY = "Pondelok";
    private static final String TUESDAY = "Utorok";
    private static final String WEDNESDAY = "Streda";
    private static final String THURSDAY = "Štvrtok";
    private static final String FRIDAY = "Piatok";
    private static final String SATURDAY = "Sobota";
    private static final String SUNDAY = "Nedeľa";

    private final String sport;
    private String note;
    private final Group group;
    private final ArrayList<Trainer> trainers;
    private final ArrayList<Athlete> attendedAthletes;
    private LocalDate date;
    private LocalTime time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AttendanceData(Group group, String sport, Trainer trainer, ArrayList<Athlete> attendedAthletes, String data, String note) {
        this.group = group;
        this.sport = sport;
        this.attendedAthletes = attendedAthletes;
        this.trainers = new ArrayList<>();
        this.trainers.add(trainer);
        this.note = note;

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
            case MONDAY_DB:
                return MONDAY;
            case TUESDAY_DB:
                return TUESDAY;
            case WEDNESDAY_DB:
                return WEDNESDAY;
            case THURSDAY_DB:
                return THURSDAY;
            case FRIDAY_DB:
                return FRIDAY;
            case SATURDAY_DB:
                return SATURDAY;
            case SUNDAY_DB:
                return SUNDAY;
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

    public ArrayList<Trainer> getTrainers() {
        return this.trainers;
    }
}
