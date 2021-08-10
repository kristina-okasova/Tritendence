package com.example.tritendence.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {
    private static final String TRAINERS_CHILD_DATABASE  = "Trainers";
    private static final String TRAINERS_EMAIL = "E-Mail";
    private static final String TRAINERS_NAME = "Name";
    private static final String TRAINERS_SURNAME = "Surname";

    private EditText email, password;
    private TextView registration;
    private Button signInBtn;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.signInBtn = findViewById(R.id.signInBtn);
        this.email = findViewById(R.id.emailSignIn);
        this.password = findViewById(R.id.passwordSIgnIn);
        this.registration = findViewById(R.id.registrationNote);
        this.authentication = FirebaseAuth.getInstance();

        this.signInBtn.setOnClickListener(v -> checkData());

        this.registration.setOnClickListener(v -> loadRegistration());
    }

    private void checkData() {
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();

        if (!emailText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            if (!passwordText.isEmpty()) {
                this.authentication.signInWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(task -> this.findUser(emailText)).
                        addOnFailureListener(task -> Toast.makeText(LogInActivity.this, "Zadané údaje nie sú správne.", Toast.LENGTH_LONG).show());

            }
            else
                this.password.setError("Je potrebné zadať heslo.");
        }
        else if (emailText.isEmpty())
            this.email.setError("Je nutné vyplniť potrebné údaje.");
        else
            this.email.setError("Zadaný e-mail nie je možné akceptovať.");
    }

    private void findUser(String emailText) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(TRAINERS_CHILD_DATABASE);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfTrainers = (int) snapshot.getChildrenCount();
                System.out.println(numberOfTrainers);
                for (int i = 1; i <= numberOfTrainers; i++) {
                    String trainersID = String.valueOf(i);
                    System.out.println(trainersID + " " + snapshot);
                    String trainersEmail = snapshot.child(trainersID).child(TRAINERS_EMAIL).getValue().toString();

                    if (trainersEmail.equals(emailText)) {
                        String trainersName = snapshot.child(trainersID).child(TRAINERS_NAME).getValue().toString();
                        String trainersSurname = snapshot.child(trainersID).child(TRAINERS_SURNAME).getValue().toString();
                        singIn(trainersName + " " + trainersSurname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void singIn(String fullName) {
        System.out.println("Signed user " + fullName);
        Intent homePage = new Intent(LogInActivity.this, HomeActivity.class);
        homePage.putExtra("SIGNED_USER", fullName);
        startActivity(homePage);
        finish();
    }

    private void loadRegistration() {
        Intent registrationPage = new Intent(LogInActivity.this, RegistrationActivity.class);
        startActivity(registrationPage);
        finish();
    }

}