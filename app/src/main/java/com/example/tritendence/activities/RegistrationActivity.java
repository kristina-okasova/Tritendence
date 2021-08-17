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
import com.example.tritendence.model.TriathlonClub;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private final static int MINIMAL_PASSWORD_LENGTH = 6;

    private TriathlonClub club;
    private EditText name, surname, email, password, passwordCheck;
    private AddEntity operator;
    public String sportText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swimmingSwitch, athleticsSwitch, cyclingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");

        this.name = findViewById(R.id.nameRegistration);
        this.surname = findViewById(R.id.surnameRegistration);
        this.email = findViewById(R.id.emailRegistration);
        this.password = findViewById(R.id.passwordRegistration);
        this.passwordCheck = findViewById(R.id.passwordCheckRegistration);
        Button registration = findViewById(R.id.registrationBtn);
        TextView signIn = findViewById(R.id.signInNote);
        this.swimmingSwitch = findViewById(R.id.swimmingSwitch);
        this.athleticsSwitch = findViewById(R.id.athleticsSwitch);
        this.cyclingSwitch = findViewById(R.id.cyclingSwitch);

        this.operator = new AddEntity();

        registration.setOnClickListener(v -> register());

        signIn.setOnClickListener(v -> loadSignIn());

        swimmingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                athleticsSwitch.setChecked(false);
                cyclingSwitch.setChecked(false);
                sportText = getString(R.string.SWIMMING);
            }
            else
                sportText = getString(R.string.EMPTY_STRING);
        });

        athleticsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                swimmingSwitch.setChecked(false);
                cyclingSwitch.setChecked(false);
                sportText = getString(R.string.ATHLETICS);
            }
            else
                sportText = getString(R.string.EMPTY_STRING);
        });

        cyclingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                swimmingSwitch.setChecked(false);
                athleticsSwitch.setChecked(false);
                sportText = getString(R.string.CYCLING);
            }
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

        if (sportText.equals(getString(R.string.EMPTY_STRING)))
            Toast.makeText(RegistrationActivity.this, getString(R.string.NO_SPORT_SELECTED), Toast.LENGTH_LONG).show();

        else if (nameText.isEmpty() || surnameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || passwordCheckText.isEmpty())
            Toast.makeText(RegistrationActivity.this, getString(R.string.EMPTY_FIELDS), Toast.LENGTH_LONG).show();

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
                        this.operator.addUser(this.club, nameText, surnameText, emailText, sportText);

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