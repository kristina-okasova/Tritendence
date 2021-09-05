package com.example.tritendence.model;

import java.io.Serializable;

public class TrainingUnit implements Serializable {
    private static final String ATHLETICS_DB = "Athletics";
    private static final String SWIMMING_DB = "Swimming";
    private static final String CYCLING_DB = "Cycling";
    private static final String STRENGTH_DB = "Strength";
    private static final String OTHER_DB = "Other";

    private static final String ATHLETICS = "Atletika";
    private static final String SWIMMING = "Plávanie";
    private static final String CYCLING = "Cyklistika";
    private static final String STRENGTH = "Sila";
    private static final String OTHER = "Iné";

    private String location, day, time, sport;
    private int groupID;

    public String getDay() {
        return this.day;
    }

    public String getTime() {
        return this.time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getSport() {
        return this.sport;
    }

    public String getLocation() {
        return location;
    }

    public String getSportTranslation() {
        String sportTranslation;
        switch (this.sport) {
            case ATHLETICS_DB:
                sportTranslation = ATHLETICS;
                break;
            case SWIMMING_DB:
                sportTranslation = SWIMMING;
                break;
            case CYCLING_DB:
                sportTranslation = CYCLING;
                break;
            case STRENGTH_DB:
                sportTranslation = STRENGTH;
                break;
            case OTHER_DB:
                sportTranslation = OTHER;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sport);
        }
        return sportTranslation;
    }

    public void setGroupID(int ID) {
        this.groupID = ID;
    }

    public int getGroupID() {
        return this.groupID;
    }
}
