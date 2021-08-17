package com.example.tritendence.model.users;

import java.util.HashMap;
import java.util.Map;

public class Trainer extends Member {
    private String sport;
    private int numberOfTrainings;

    public Trainer(int ID, String name, String surname, String email, String sport, int numberOfTrainings) {
        super(ID, name, surname, email);
        this.sport = sport;
        this.numberOfTrainings = numberOfTrainings;
    }

    public Map<String, Object> getMappedData() {
        Map<String, Object> data = new HashMap<String, Object>();

        data.put("Name", this.name);
        data.put("Surname", this.surname);
        data.put("E-Mail", this.email);
        data.put("Sport", this.sport);
        data.put("NumberOfTrainings", 0);

        return data;
    }

    public int getNumberOfTrainings() {
        return this.numberOfTrainings;
    }

    public String getIDText() {
        return String.valueOf(this.ID);
    }
}
