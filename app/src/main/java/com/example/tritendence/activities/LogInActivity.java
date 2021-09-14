package com.example.tritendence.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
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
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;

    //Layout's items.
    private Button signInBtn;
    private TextView registration;
    private EditText email, password;

    private FirebaseAuth authentication;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Loading data from database.
        this.loadData = new LoadData(this);
        this.club = this.loadData.getClub();
        this.initializeLayoutItems();

        //Setting onclick listeners for sign in button and registration text.
        signInBtn.setOnClickListener(v -> checkData());
        registration.setOnClickListener(v -> loadRegistration());
    }

    private void initializeLayoutItems() {
        this.signInBtn = findViewById(R.id.signInBtn);
        this.registration = findViewById(R.id.registrationNote);
        this.email = findViewById(R.id.emailSignIn);
        this.password = findViewById(R.id.passwordSIgnIn);
        this.authentication = FirebaseAuth.getInstance();
    }

    private void checkData() {
        //Checking if the application has Internet connection.
        if (!this.isOnline()) {
            Toast.makeText(this, getString(R.string.INTERNET_CONNECTION_REQUIRED), Toast.LENGTH_LONG).show();
            return;
        }
        //Getting email and password of the user.
        String emailText = this.email.getText().toString();
        String passwordText = this.password.getText().toString();

        //Checking if email and password are filled in.
        if (!emailText.isEmpty() && !passwordText.isEmpty()) {
            //Authenticate the user by filled in email and password. On success Home Activityis displayed. Otherwise the Toast is shown.
            this.authentication.signInWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(task -> this.findUser(emailText)).
                    addOnFailureListener(task -> Toast.makeText(LogInActivity.this, getString(R.string.WRONG_SIGN_IN_DATA), Toast.LENGTH_LONG).show());
        }
        else if (emailText.isEmpty())
            this.email.setError(getString(R.string.REQUIRED_DATA_MISSING));
        else
            this.password.setError(getString(R.string.PASSWORD_REQUIRED));
    }

    private void findUser(String emailText) {
        //Checking if the signed user is a trainer.
        for (Member member : this.loadData.getClub().getMembersOfClub()) {
            if (member instanceof Trainer && (((Trainer) member).getEmail()).equals(emailText))
                signIn(member.getFullName());
        }
        //Checking if the signed user is Admin.
        if (this.loadData.getClub().getAdminOfClub().getEmail().equals(emailText))
            signIn(this.loadData.getClub().getAdminOfClub().getFullName());
    }

    private void signIn(String fullName) {
        //Creating new intent of Home Activity when the user is signed in.
        Intent homePage = new Intent(LogInActivity.this, HomeActivity.class);
        homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), fullName);
        homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
        startActivity(homePage);
        finish();
    }

    private void loadRegistration() {
        //Checking if the application has Internet connection.
        if (!this.isOnline()) {
            Toast.makeText(this, getString(R.string.INTERNET_CONNECTION_REQUIRED), Toast.LENGTH_LONG).show();
            return;
        }
        //Creating new intent of Registration Activity to create new user's account.
        Intent registrationPage = new Intent(LogInActivity.this, RegistrationActivity.class);
        registrationPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        startActivity(registrationPage);
        finish();
    }

    private boolean isOnline() {
        //Checking Internet connection by ping.
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

    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
    }

}