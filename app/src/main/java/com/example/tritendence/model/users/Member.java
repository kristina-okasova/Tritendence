package com.example.tritendence.model.users;

import java.io.Serializable;

public class Member implements Serializable {
    protected String name, surname, groupID;
    protected int ID;

    public Member() {}

    public Member(int ID, String name, String surname) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
    }

    public String getFullName() {
        return this.name + " " + this.surname;
    }

    public String getSurname() {
        return this.surname;
    }

    public int getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }
}
