package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.AddEntity;
import com.example.tritendence.model.LoadData;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private final static int MINIMAL_PASSWORD_LENGTH = 6;

    private EditText name, surname, email, password, passwordCheck;
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
        Button registration = findViewById(R.id.registrationBtn);
        TextView signIn = findViewById(R.id.signInNote);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch swimmingSwitch = findViewById(R.id.swimmingSwitch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch athleticsSwitch = findViewById(R.id.athleticsSwitch);

        LoadData loadData = new LoadData();
        this.operator = new AddEntity(loadData.getClub());

        registration.setOnClickListener(v -> register());

        signIn.setOnClickListener(v -> loadSignIn());

        swimmingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                sportText = getString(R.string.SWIMMING);
            else
                sportText = getString(R.string.EMPTY_STRING);
        });

        athleticsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                sportText = getString(R.string.ATHLETICS);
            else
                sportText = getString(R.string.EMPTY_STRING);
        });
    }

    private void register() {
        String nameText = this.name.getText().toString();
        String surnameText = this.surname.getText().toString();
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();
        String passwordCheckText = this.passwordCheck.getText().toString();

        FirebaseAuth authentication = FirebaseAuth.getInstance();

        if (this.sportText.equals(""))
            Toast.makeText(RegistrationActivity.this, "", Toast.LENGTH_LONG).show();

        else if (nameText.isEmpty() || surnameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || passwordCheckText.isEmpty())
            Toast.makeText(RegistrationActivity.this, getString(R.string.ONLY_ONE_SPORT_REQUIRED), Toast.LENGTH_LONG).show();

        else if (!passwordCheckText.matches(passwordText))
            this.passwordCheck.setError(getString(R.string.REQUIRED_MATCH_OF_PASSWORDS));

        else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
            this.email.setError(getString(R.string.NOT_ACCEPTABLE_EMAIL));

        else if (passwordText.length() < MINIMAL_PASSWORD_LENGTH)
            this.password.setError(getString(R.string.TOO_SHORT_PASSWORD));

        else {
            authentication.createUserWithEmailAndPassword(emailText, passwordText).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        this.operator.addUser(nameText, surnameText, emailText, sportText);

                        Toast.makeText(RegistrationActivity.this, getString(R.string.SUCCESSFUL_REGISTRATION), Toast.LENGTH_LONG).show();
                        loadSignIn();
                        finish();
                    }
                    else
                        Toast.makeText(RegistrationActivity.this, getString(R.string.UNSUCCESSFUL_REGISTRATION), Toast.LENGTH_LONG).show();
                });
        }
    }

    private void loadSignIn() {
        Intent signInPage = new Intent(RegistrationActivity.this, LogInActivity.class);
        startActivity(signInPage);
    }
}