package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tritendence.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity {
    private EditText emailText, passwordText;
    private Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.signInBtn = findViewById(R.id.signInBtn);
        this.emailText = findViewById(R.id.emailSignIn);
        this.passwordText = findViewById(R.id.passwordSIgnIn);

        this.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        String email = this.emailText.getText().toString();
        String password = this.passwordText.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!password.isEmpty()) {
                Toast msg = Toast.makeText(LogInActivity.this, "Úspešné prihlásenie.", Toast.LENGTH_LONG);
                msg.show();

                Intent home = new Intent(LogInActivity.this, HomeActivity.class);
                startActivity(home);
            }
            else {
                Toast msg = Toast.makeText(LogInActivity.this, "Je potrebné zadať heslo.", Toast.LENGTH_LONG);
                msg.show();
            }
        } else if (email.isEmpty()) {
            Toast msg = Toast.makeText(LogInActivity.this, "Je nutné vyplniť potrebné údaje.", Toast.LENGTH_LONG);
            msg.show();
        }
        else {
            Toast msg = Toast.makeText(LogInActivity.this, "Zadaný e-mail nie je možné akceptovať.", Toast.LENGTH_LONG);
            msg.show();
        }
    }
}