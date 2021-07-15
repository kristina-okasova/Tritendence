package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;

public class RegistrationActivity extends AppCompatActivity {
    private EditText name, surname, email, password, passwordCheck;
    private Button registration;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        this.name = findViewById(R.id.nameRegistration);
        this.surname = findViewById(R.id.surnameRegistration);
        this.email = findViewById(R.id.emailRegistration);
        this.password = findViewById(R.id.passwordRegistration);
        this.passwordCheck = findViewById(R.id.passwordCheckRegistration);
        this.registration = findViewById(R.id.registrationBtn);
        this.signIn = findViewById(R.id.signInNote);

        this.registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        this.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSignIn();
            }
        });
    }

    private void register() {
        String nameText = this.name.getText().toString();
        String surnameText = this.surname.getText().toString();
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();
        String passwordCheckText = this.passwordCheck.getText().toString();

        if (nameText.isEmpty() || surnameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || passwordCheckText.isEmpty()) {
            Toast msg = Toast.makeText(RegistrationActivity.this, "Je nutné vyplniť všetky potrebné údaje.", Toast.LENGTH_LONG);
            msg.show();
        }
        else if (!passwordCheckText.matches(passwordText)) {
            Toast msg = Toast.makeText(RegistrationActivity.this, "Zadané heslá sa musia zhodovať.", Toast.LENGTH_LONG);
            msg.show();
        }
        else {
            Toast msg = Toast.makeText(RegistrationActivity.this, "Registrácia bola úspešná.", Toast.LENGTH_LONG);
            msg.show();

            this.loadSignIn();
        }
    }

    private void loadSignIn() {
        Intent signInPage = new Intent(RegistrationActivity.this, LogInActivity.class);
        startActivity(signInPage);
    }
}