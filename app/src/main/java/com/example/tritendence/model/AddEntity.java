package com.example.tritendence.model;

import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AddEntity {
    private final static String TRAINERS_CHILD_DATABASE = "Trainers";

    public AddEntity() {}

    public void addUser(TriathlonClub club, String name, String surname, String email, String sport) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        club.updateNumberOfTrainers();
        Trainer trainer = new Trainer(club.getNumberOfTrainers(), name.trim(), surname.trim(), email.trim(), sport, 0, "DarkRed");
        Map<String, Object> userData = trainer.getMappedData();
        root.child(TRAINERS_CHILD_DATABASE + "/" + trainer.getIDText()).setValue(userData);
    }
}
