package com.example.tritendence.model.users;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;

public class Athlete extends Member {
    private int groupID;
    private int numberOfTrainings;
    private LocalDate dayOfBirth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Athlete(int ID, String name, String surname, int groupID, int numberOfTrainings, String dayOfBirth) {
        super(ID, name, surname);
        this.groupID = groupID;
        this.numberOfTrainings = numberOfTrainings;

        this.setDayOfBirth(dayOfBirth);
    }

    public int getGroupID() {
        return this.groupID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getNumberOfTrainings() {
        return this.numberOfTrainings;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDayOfBirth(String dayOfBirth) {
        int year = Integer.parseInt(dayOfBirth.substring(6, 10));
        int month = Integer.parseInt(dayOfBirth.substring(3, 5));
        int day = Integer.parseInt(dayOfBirth.substring(0, 2));
        this.dayOfBirth = LocalDate.of(year, month, day);
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDayOfBirth() {
        return String.format("%02d.%02d.%d", this.dayOfBirth.getDayOfMonth(), this.dayOfBirth.getMonthValue(), this.dayOfBirth.getYear());
    }

    public LocalDate getDate() {
        return this.dayOfBirth;
    }

    public void addAttendedTraining() {
        this.numberOfTrainings++;
    }
}
