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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity {
    private EditText email, password;
    private TextView registration;
    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.signInBtn = findViewById(R.id.signInBtn);
        this.email = findViewById(R.id.emailSignIn);
        this.password = findViewById(R.id.passwordSIgnIn);
        this.registration = findViewById(R.id.registrationNote);

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
                Toast msg = Toast.makeText(LogInActivity.this, "Úspešné prihlásenie.", Toast.LENGTH_LONG);
                msg.show();

                Intent homePage = new Intent(LogInActivity.this, HomeActivity.class);
                startActivity(homePage);
            }
            else {
                Toast msg = Toast.makeText(LogInActivity.this, "Je potrebné zadať heslo.", Toast.LENGTH_LONG);
                msg.show();
            }
        } else if (emailText.isEmpty()) {
            Toast msg = Toast.makeText(LogInActivity.this, "Je nutné vyplniť potrebné údaje.", Toast.LENGTH_LONG);
            msg.show();
        }
        else {
            Toast msg = Toast.makeText(LogInActivity.this, "Zadaný e-mail nie je možné akceptovať.", Toast.LENGTH_LONG);
            msg.show();
        }
    }

    private void loadRegistration() {
        Intent registrationPage = new Intent(LogInActivity.this, RegistrationActivity.class);
        startActivity(registrationPage);
    }

}