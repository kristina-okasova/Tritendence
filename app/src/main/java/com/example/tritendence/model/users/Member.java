package com.example.tritendence.model.users;

import java.io.Serializable;

public class Member implements Serializable {
    protected String name, surname, email, groupID;
    protected int ID;

    public Member() {}

    public Member(int ID, String name, String surname, String email) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getFullName() {
        return this.name + " " + this.surname;
    }

    public String getEmail() {
        return this.email;
    }

    public String getSurname() {
        return this.surname;
    }
}
