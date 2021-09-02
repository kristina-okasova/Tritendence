package com.example.tritendence.model.users;

public class Admin extends Member {
    private String email;
    private int numberOfTrainings;
    public Admin(int ID, String name, String surname, String email, int numberOfTrainings) {
        super(ID, name, surname);
        this.email = email;
        this.numberOfTrainings = numberOfTrainings;
    }

    public String getEmail() {
        return this.email;
    }

    public int getNumberOfTrainings() {
        return this.numberOfTrainings;
    }
}
