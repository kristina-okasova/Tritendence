package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.AddEntity;
import com.example.tritendence.model.TriathlonClub;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private final static int MINIMAL_PASSWORD_LENGTH = 6;

    //Intent's extras
    private TriathlonClub club;

    //Layout's items
    private EditText name, surname, email, password, passwordCheck;
    private Button registration;
    private TextView signIn;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swimmingSwitch, athleticsSwitch, cyclingSwitch;

    public String sportText;
    private AddEntity operator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_DarkRed);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Getting extras of the intent.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.initializeLayoutItems();
        this.operator = new AddEntity(this.club);

        //Setting listeners on switch buttons to set currently selected type of sport.
        this.setListenerForSwitch(this.swimmingSwitch, this.athleticsSwitch, this.cyclingSwitch, getString(R.string.SWIMMING));
        this.setListenerForSwitch(this.athleticsSwitch, this.swimmingSwitch, this.cyclingSwitch, getString(R.string.ATHLETICS));
        this.setListenerForSwitch(this.cyclingSwitch, this.swimmingSwitch, this.athleticsSwitch, getString(R.string.CYCLING));

        //Setting onclick listeners on registration button and sign in note.
        this.registration.setOnClickListener(v -> register());
        this.signIn.setOnClickListener(v -> loadSignIn());
    }

    private void initializeLayoutItems() {
        this.name = findViewById(R.id.nameRegistration);
        this.surname = findViewById(R.id.surnameRegistration);
        this.email = findViewById(R.id.emailRegistration);
        this.password = findViewById(R.id.passwordRegistration);
        this.passwordCheck = findViewById(R.id.passwordCheckRegistration);
        this.registration = findViewById(R.id.registrationBtn);
        this.signIn = findViewById(R.id.signInNote);
        this.swimmingSwitch = findViewById(R.id.swimmingSwitch);
        this.athleticsSwitch = findViewById(R.id.athleticsSwitch);
        this.cyclingSwitch = findViewById(R.id.cyclingSwitch);
    }

    private void setListenerForSwitch(@SuppressLint("UseSwitchCompatOrMaterialCode") Switch firstSwitch, @SuppressLint("UseSwitchCompatOrMaterialCode") Switch secondSwitch, @SuppressLint("UseSwitchCompatOrMaterialCode") Switch thirdSwitch, String sport) {
        firstSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                secondSwitch.setChecked(false);
                thirdSwitch.setChecked(false);
                this.sportText = sport;
            }
            else
                this.sportText = getString(R.string.EMPTY_STRING);
        });
    }

    private void register() {
        //Getting all information about new user.
        String nameText = this.name.getText().toString();
        String surnameText = this.surname.getText().toString();
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();
        String passwordCheckText = this.passwordCheck.getText().toString();

        FirebaseAuth authentication = FirebaseAuth.getInstance();
        //Checking if a type of sport is selected.
        if (sportText.equals(getString(R.string.EMPTY_STRING)))
            Toast.makeText(RegistrationActivity.this, getString(R.string.NO_SPORT_SELECTED), Toast.LENGTH_LONG).show();

        //Checking if all the data are filled in.
        else if (nameText.isEmpty() || surnameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || passwordCheckText.isEmpty())
            Toast.makeText(RegistrationActivity.this, getString(R.string.EMPTY_FIELDS), Toast.LENGTH_LONG).show();

        //Checking if filled passwords match.
        else if (!passwordCheckText.matches(passwordText))
            this.passwordCheck.setError(getString(R.string.REQUIRED_MATCH_OF_PASSWORDS));

        //Checking if filled in email fulfils the email pattern.
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
            this.email.setError(getString(R.string.NOT_ACCEPTABLE_EMAIL));

        //Checking if the password's length is sufficient.
        else if (passwordText.length() < MINIMAL_PASSWORD_LENGTH)
            this.password.setError(getString(R.string.TOO_SHORT_PASSWORD));

        //In case that all the requirements are met, the user is registered.
        else {
            authentication.createUserWithEmailAndPassword(emailText, passwordText).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Adding new user to the club.
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
        //Creating new intent of Log In Activity to sign in.
        Intent signInPage = new Intent(RegistrationActivity.this, LogInActivity.class);
        startActivity(signInPage);
    }
}