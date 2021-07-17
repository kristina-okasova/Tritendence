package com.example.tritendence.model.users;

import java.util.HashMap;
import java.util.Map;

public class Trainer extends Member {
    private String sport;

    public Trainer(int ID, String name, String surname, String email, String sport) {
        super(ID, name, surname, email);
        this.sport = sport;
    }

    public Map<String, Object> getMappedData() {
        Map<String, Object> data = new HashMap<String, Object>();

        data.put("Name", this.name);
        data.put("Surname", this.surname);
        data.put("E-mail", this.email);
        data.put("Sport", this.sport);

        return data;
    }

    public String getIDText() {
        return String.valueOf(this.ID);
    }
}
