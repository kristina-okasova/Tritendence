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
}
