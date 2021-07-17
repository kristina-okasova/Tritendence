package com.example.tritendence.model;

public class TriathlonClub {
    private int numberOfUsers;

    public TriathlonClub() {
        this.numberOfUsers = 0;
    }

    public void updateNumberOfUsers() {
        this.numberOfUsers++;
    }

    public int getNumberOfUsers() {
        return this.numberOfUsers;
    }
}
