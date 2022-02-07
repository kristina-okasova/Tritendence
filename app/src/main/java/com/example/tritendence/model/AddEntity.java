package com.example.tritendence.model;

import com.example.tritendence.model.users.Trainer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AddEntity {
    private final static String TRAINERS_CHILD_DATABASE = "Trainers";
    private final static String FIRST_CHILD_DB = "-1";

    private final TriathlonClub club;

    public AddEntity(TriathlonClub club) {
        this.club = club;
    }

    public void addUser(TriathlonClub club, String name, String surname, String email, String sport) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference root = database.getReference();

        Trainer trainer = new Trainer(this.club.getNumberOfTrainers() + 1, name.trim(), surname.trim(), email.trim(), sport, "DarkRed");
        Map<String, Object> userData = trainer.getMappedData();
        root.child(TRAINERS_CHILD_DATABASE + "/" + trainer.getIDText()).setValue(userData);

        if (this.club.getNumberOfTrainers() == 0)
            root.child(TRAINERS_CHILD_DATABASE + "/" + FIRST_CHILD_DB).removeValue();
        club.updateNumberOfTrainers();
    }
}
