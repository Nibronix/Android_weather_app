package com.example.weather_app;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.weather_app.Database.AppDatabase;
import com.example.weather_app.DAOs.UserDAO;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private UserDAO userDAO;
    private static final String TAG = "Cheesecake";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If user is already logged in, skip the login page
        if (isUserLoggedIn()) {
            goToWeatherDashboard();
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Initialize views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // Initialize database and DAO
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        userDAO = db.userDAO();

        // Handle login button click
        loginButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            new Thread(() -> {
                User user = userDAO.getUserByUsernameandPassword(username, password);
                runOnUiThread(() -> {
                    if (user != null) {
                        loginUser(user.getUserId());
                        goToWeatherDashboard();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    private void loginUser(int userId) {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("logged_in_user_id", userId);
        editor.apply();
        Log.d(TAG, "User logged in with ID: " + userId);
    }


    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String loggedInUser = prefs.getString("logged_in_user", null);
        return loggedInUser != null;
    }


    private void goToWeatherDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
