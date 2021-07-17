package com.example.tritendence.model.users;

public class Member {
    protected String name, surname, email;
    protected int ID;

    public Member(int ID, String name, String surname, String email) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
