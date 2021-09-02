package com.example.tritendence.model.users;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;

public class Athlete extends Member {
    private int groupID, numberOfTrainings;
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

    public String getDayOfBirth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return this.dayOfBirth.getDayOfMonth() + "." + this.dayOfBirth.getMonthValue() + "." + this.dayOfBirth.getYear();
        return null;
    }

    public LocalDate getDate() {
        return this.dayOfBirth;
    }
}
