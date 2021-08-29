package com.example.tritendence.model.users;

public class Admin extends Member {
    private int numberOfTrainings;
    public Admin(int ID, String name, String surname, String email, int numberOfTrainings) {
        super(ID, name, surname, email);
        this.numberOfTrainings = numberOfTrainings;
    }

    public int getNumberOfTrainings() {
        return this.numberOfTrainings;
    }
}
