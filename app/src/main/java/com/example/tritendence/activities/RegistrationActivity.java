package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.AddEntity;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private final static int MINIMAL_PASSWORD_LENGTH = 6;

    private EditText name, surname, email, password, passwordCheck;
    private Button registration;
    private TextView signIn;
    private Switch swimmingSwitch, athleticsSwitch;
    private FirebaseAuth authentication;
    private AddEntity operator;
    public String sportText;

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
        this.swimmingSwitch = findViewById(R.id.swimmingSwitch);
        this.athleticsSwitch = findViewById(R.id.athleticsSwitch);
        this.operator = new AddEntity();

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

        this.swimmingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                sportText = "Plávanie";
            else
                sportText = "";
        });

        this.athleticsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                sportText = "Atletika";
            else
                sportText = "";
        });
    }

    private void register() {
        String nameText = this.name.getText().toString();
        String surnameText = this.surname.getText().toString();
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();
        String passwordCheckText = this.passwordCheck.getText().toString();

        this.authentication = FirebaseAuth.getInstance();

        if (this.sportText.equals(""))
            Toast.makeText(RegistrationActivity.this, "Je potrebné vybrať práve jeden šport.", Toast.LENGTH_LONG).show();

        else if (nameText.isEmpty() || surnameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || passwordCheckText.isEmpty())
            Toast.makeText(RegistrationActivity.this, "Je nutné zadať všetky potrebné údaje.", Toast.LENGTH_LONG).show();

        else if (!passwordCheckText.matches(passwordText))
            this.passwordCheck.setError("Zadané heslá sa musia zhodovať.");

        else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
            this.email.setError("Zadaný e-mail nie je možné akceptovať.");

        else if (passwordText.length() < MINIMAL_PASSWORD_LENGTH)
            this.password.setError("Heslo musí pozostávať minimálne zo 6 znakov.");

        else {
            this.authentication.createUserWithEmailAndPassword(emailText, passwordText).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        this.operator.addUser(nameText, surnameText, emailText, sportText);

                        Toast.makeText(RegistrationActivity.this, "Úspešná registrácia.", Toast.LENGTH_LONG).show();
                        loadSignIn();
                        finish();
                    }
                    else
                        Toast.makeText(RegistrationActivity.this, "Neúspešná registrácia.", Toast.LENGTH_LONG).show();
                });
        }
    }

    private void loadSignIn() {
        Intent signInPage = new Intent(RegistrationActivity.this, LogInActivity.class);
        startActivity(signInPage);
    }
}