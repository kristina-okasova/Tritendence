package com.example.tritendence.model.users;

import java.util.HashMap;
import java.util.Map;

public class Trainer extends Member {

    private final String email;
    private final String sport;
    private String theme;

    public Trainer(int ID, String name, String surname, String email, String sport, String theme) {
        super(ID, name, surname);
        this.email = email;
        this.sport = sport;
        this.theme = theme;
    }

    public Map<String, Object> getMappedData() {
        Map<String, Object> data = new HashMap<>();

        data.put("Name", this.name);
        data.put("Surname", this.surname);
        data.put("E-Mail", this.email);
        data.put("Sport", this.sport);
        data.put("Theme", this.theme);

        return data;
    }

    public String getEmail() {
        return this.email;
    }

    public String getIDText() {
        return String.valueOf(this.ID);
    }

    public String getSportTranslation() {
        String sportTranslation;
        switch (this.sport) {
            case "Atletika":
                sportTranslation = "Athletics";
                break;
            case "Plávanie":
                sportTranslation = "Swimming";
                break;
            case "Cyklistika":
                sportTranslation = "Cycling";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sport);
        }
        return sportTranslation;
    }

    public String getSport() {
        return this.sport;
    }

    public String getTheme() {
        return this.theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
