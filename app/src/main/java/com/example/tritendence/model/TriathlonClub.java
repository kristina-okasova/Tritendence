package com.example.tritendence.model;

public class TriathlonClub {
    private int numberOfUsers;
    private int numberOfGroups = 3;

    public TriathlonClub() {
        this.numberOfUsers = 0;
    }

    public void updateNumberOfUsers() {
        this.numberOfUsers++;
    }

    public int getNumberOfUsers() {
        return this.numberOfUsers;
    }

    public int getNumberOfGroups() {
        return this.numberOfGroups;
    }
}
