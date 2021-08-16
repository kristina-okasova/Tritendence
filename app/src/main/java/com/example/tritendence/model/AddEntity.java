package com.example.tritendence.model;

import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AddEntity {
    private final static String TRAINERS_CHILD_DATABASE = "Trainers";
    private final TriathlonClub club;

    public AddEntity(TriathlonClub club) {
        this.club = club;
    }

    public void addUser(String name, String surname, String email, String sport) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        this.club.updateNumberOfUsers();
        Trainer trainer = new Trainer(this.club.getNumberOfUsers(), name, surname, email, sport, 0);
        Map<String, Object> userData = trainer.getMappedData();
        root.child(TRAINERS_CHILD_DATABASE + "/" + trainer.getIDText()).setValue(userData);
    }
}
