package com.example.tritendence.model.users;

public class Admin extends Member {
    private final String email;
    private String sport, theme;
    private final int numberOfTrainings;

    public Admin(int ID, String name, String surname, String email, int numberOfTrainings, String theme) {
        super(ID, name, surname);
        this.email = email;
        this.numberOfTrainings = numberOfTrainings;
        this.theme = theme;
        this.sport = "";
    }

    public String getEmail() {
        return this.email;
    }

    public int getNumberOfTrainings() {
        return this.numberOfTrainings;
    }

    public String getSport() {
        return this.sport;
    }

    public String getTheme() {
        return this.theme;
    }
}
