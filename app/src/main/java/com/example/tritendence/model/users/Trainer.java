package com.example.tritendence.model.users;

import com.example.tritendence.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Trainer extends Member {
    private static final String TRAINERS_CHILD_DB = "Trainers";
    private static final String NUMBER_OF_TRAININGS = "NumberOfTrainings";

    private final String email;
    private String sport, theme;
    private int numberOfTrainings;

    public Trainer(int ID, String name, String surname, String email, String sport, int numberOfTrainings, String theme) {
        super(ID, name, surname);
        this.email = email;
        this.sport = sport;
        this.numberOfTrainings = numberOfTrainings;
        this.theme = theme;
    }

    public Map<String, Object> getMappedData() {
        Map<String, Object> data = new HashMap<>();

        data.put("Name", this.name);
        data.put("Surname", this.surname);
        data.put("E-Mail", this.email);
        data.put("Sport", this.sport);
        data.put("NumberOfTrainings", 0);
        data.put("Theme", this.theme);

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

    public void addTraining() {
        this.numberOfTrainings++;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();
        root.child(TRAINERS_CHILD_DB + "/" + this.ID + "/" + NUMBER_OF_TRAININGS).setValue(this.numberOfTrainings);
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
