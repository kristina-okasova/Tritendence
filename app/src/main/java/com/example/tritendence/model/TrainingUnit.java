package com.example.tritendence.model;

import java.io.Serializable;

public class TrainingUnit implements Serializable {
    private String location, day, time, sport;

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
            case "Athetics":
                sportTranslation = "Atletika";
                break;
            case "Swimming":
                sportTranslation = "Plávanie";
                break;
            case "Cycling":
                sportTranslation = "Cyklistika";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sport);
        }
        return sportTranslation;
    }
}
