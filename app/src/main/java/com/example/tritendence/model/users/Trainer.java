package com.example.tritendence.model.users;

import com.example.tritendence.R;

import java.util.HashMap;
import java.util.Map;

import static android.provider.Settings.System.getString;

public class Trainer extends Member {
    private String email, sport;
    private int numberOfTrainings;

    public Trainer(int ID, String name, String surname, String email, String sport, int numberOfTrainings) {
        super(ID, name, surname);
        this.email = email;
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

    public String getEmail() {
        return this.email;
    }

    public int getNumberOfTrainings() {
        return this.numberOfTrainings;
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
}
