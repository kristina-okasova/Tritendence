package com.example.tritendence.activities;

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

public class LogInActivity extends AppCompatActivity {
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

        this.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        this.registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRegistration();
            }
        });
    }

    private void signIn() {
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();

        if (!emailText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            if (!passwordText.isEmpty()) {
                this.authentication.signInWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(task -> {
                    Intent homePage = new Intent(LogInActivity.this, HomeActivity.class);
                    startActivity(homePage);
                    finish();
                }).addOnFailureListener(task -> Toast.makeText(LogInActivity.this, "Zadané údaje nie sú správne.", Toast.LENGTH_LONG).show());

            }
            else
                this.password.setError("Je potrebné zadať heslo.");
        }
        else if (emailText.isEmpty())
            this.email.setError("Je nutné vyplniť potrebné údaje.");
        else
            this.email.setError("Zadaný e-mail nie je možné akceptovať.");
    }

    private void loadRegistration() {
        Intent registrationPage = new Intent(LogInActivity.this, RegistrationActivity.class);
        startActivity(registrationPage);
        finish();
    }

}