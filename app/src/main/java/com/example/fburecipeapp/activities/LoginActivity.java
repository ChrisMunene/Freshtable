package com.example.fburecipeapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fburecipeapp.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText usernameInput;
    private EditText passwordInput;
    private ParseUser currentUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ParseUser.getCurrentUser();

        // allows persistance of user when app is reopened
        if (currentUser!= null) {
            final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_login);

            // Initialize Progress Dialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait.");
            progressDialog.setCancelable(false);

            loginBtn = findViewById(R.id.loginBtn);
            usernameInput = findViewById(R.id.username_et);
            passwordInput = findViewById(R.id.password_et);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = usernameInput.getText().toString();
                    final String password = passwordInput.getText().toString();

                    login(username, password);
                }
            });
        }




    }

    // creates new Parse user and logs in
    private void login(String username, String password) {
        progressDialog.show();
        // Invoke background login
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException loginException) {
                //on success -- Redirect to CreatePostActivity
                if (loginException == null) {
                    Log.d("LoginActivity", "Login Successful");
                    final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login Failure");
                    loginException.printStackTrace();
                }

                progressDialog.dismiss();
            }
        });
    }
}
