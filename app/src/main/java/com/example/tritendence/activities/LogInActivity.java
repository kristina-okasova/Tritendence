package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tritendence.R;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.users.Member;
import com.example.tritendence.model.users.Trainer;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class LogInActivity extends AppCompatActivity {
    private TriathlonClub club;
    private LoadData loadData;
    private EditText email, password;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loadData = new LoadData();
        this.club = this.loadData.getClub();

        Button signInBtn = findViewById(R.id.signInBtn);
        TextView registration = findViewById(R.id.registrationNote);
        this.email = findViewById(R.id.emailSignIn);
        this.password = findViewById(R.id.passwordSIgnIn);
        this.authentication = FirebaseAuth.getInstance();

        signInBtn.setOnClickListener(v -> checkData());
        registration.setOnClickListener(v -> loadRegistration());
    }

    private void checkData() {
        if (!this.isOnline()) {
            Toast.makeText(this, getString(R.string.INTERNET_CONNECTION_REQUIRED), Toast.LENGTH_LONG).show();
            return;
        }
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();

        if (!emailText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            if (!passwordText.isEmpty()) {
                this.authentication.signInWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(task -> this.findUser(emailText)).
                        addOnFailureListener(task -> Toast.makeText(LogInActivity.this, getString(R.string.WRONG_SIGN_IN_DATA), Toast.LENGTH_LONG).show());

            }
            else
                this.password.setError(getString(R.string.PASSWORD_REQUIRED));
        }
        else if (emailText.isEmpty())
            this.email.setError(getString(R.string.REQUIRED_DATA_MISSING));
        else
            this.email.setError(getString(R.string.NOT_ACCEPTABLE_EMAIL));
    }

    private void findUser(String emailText) {
        for (Member member : this.loadData.getClub().getMembersOfClub()) {
            if (member instanceof Trainer && (((Trainer) member).getEmail()).equals(emailText))
                singIn(member.getFullName());
        }
    }

    private void singIn(String fullName) {
        Intent homePage = new Intent(LogInActivity.this, HomeActivity.class);
        homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), fullName);
        homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        startActivity(homePage);
        finish();
    }

    private void loadRegistration() {
        if (!this.isOnline()) {
            Toast.makeText(this, getString(R.string.INTERNET_CONNECTION_REQUIRED), Toast.LENGTH_LONG).show();
            return;
        }
        Intent registrationPage = new Intent(LogInActivity.this, RegistrationActivity.class);
        registrationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        startActivity(registrationPage);
        finish();
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}