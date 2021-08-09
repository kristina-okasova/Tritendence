package com.example.tritendence.model.users;

public class Athlete extends Member {
    private int groupID;

    public Athlete() {}

    public Athlete(int ID, String name, String surname, String email, int groupID) {
        super(ID, name, surname, email);
        this.groupID = groupID;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
