package com.example.fburecipeapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

<<<<<<< HEAD:app/src/main/java/com/example/fburecipeapp/LoginActivity.java
import androidx.appcompat.app.AppCompatActivity;

=======
import com.example.fburecipeapp.R;
>>>>>>> 803096f... Scanner - Add dialog for editing list items.:app/src/main/java/com/example/fburecipeapp/activities/LoginActivity.java
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText usernameInput;
    private EditText passwordInput;
<<<<<<< HEAD:app/src/main/java/com/example/fburecipeapp/LoginActivity.java
    private ParseUser currentUser;
=======
    private ProgressDialog pd;
>>>>>>> 803096f... Scanner - Add dialog for editing list items.:app/src/main/java/com/example/fburecipeapp/activities/LoginActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        usernameInput = findViewById(R.id.username_et);
        passwordInput = findViewById(R.id.password_et);
        currentUser = ParseUser.getCurrentUser();

        if (currentUser!= null) {
            final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            final String username = usernameInput.getText().toString();
            final String password = passwordInput.getText().toString();
            login(username, password);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                login(username, password);
            }
        });

        // Initialize Progress Dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
    }

    private void login(String username, String password) {
        pd.show();
        // Invoke background login
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //on success -- Redirect to CreatePostActivity
                if(e == null){
                    Log.d("LoginActivity", "Login Successful");
                    final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    // on failure
                    Log.e("LoginActivity", "Login Failure");
                    e.printStackTrace();
                }

                pd.dismiss();
            }
        });
    }
}
