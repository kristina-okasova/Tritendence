package com.example.tritendence.model.groups;

import com.example.tritendence.model.TrainingUnit;
import com.example.tritendence.model.users.Athlete;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
    private final int ID;
    private String name;
    private final String category;
    private final ArrayList<TrainingUnit> timetable;
    private final ArrayList<Athlete> athletesOfGroup;

    public Group(int ID, String name, String category) {
        this.ID = ID;
        this.name = name;
        this.category = category;

        this.timetable = new ArrayList<>();
        this.athletesOfGroup = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTrainingUnit(TrainingUnit unit) {
        this.timetable.add(unit);
    }

    public void addAthlete(Athlete athlete) {
        this.athletesOfGroup.add(athlete);
    }

    public ArrayList<TrainingUnit> getTimetable() {
        return this.timetable;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return category;
    }

    public int getID() {
        return this.ID;
    }

    public ArrayList<Athlete> getAthletesOfGroup() {
        return this.athletesOfGroup;
    }

    public ArrayList<String> getNamesOfAthletesOfGroup() {
        ArrayList<String> namesOfAthletes = new ArrayList<>();
        for (Athlete athlete : this.athletesOfGroup) {
            namesOfAthletes.add(athlete.getFullName());
        }

        return namesOfAthletes;
    }

    public int getNumberOfAthletes() {
        return this.athletesOfGroup.size();
    }

    public void findAthletes() {

    }
}
