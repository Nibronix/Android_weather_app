
package com.example.weather_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.weather_app.DAOs.CityDAO;
import com.example.weather_app.Database.AppDatabase;
import com.example.weather_app.DAOs.UserDAO;

public class LoginActivity extends AppCompatActivity {


    private EditText usernameInput, passwordInput, confirmPasswordInput;
    private Button loginButton, registerButton, toggleButton;
    private UserDAO userDAO;
    private static final String TAG = "Cheesecake";
    private boolean isRegisterMode = false;

    private static final  String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD ="password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "LoginActivity onCreate");
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            goToMainActivity();
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Initialize views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        toggleButton = findViewById(R.id.toggleButton);

        // Initialize database and DAO
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        userDAO = db.userDAO();
        Log.d(TAG, "Database and DAO initialized: db = " + db + ", userDAO = " + userDAO);

        // Handle login button click
        loginButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                // Admin login
                loginUser(-1);
                goToAdminDashboard(true);
            } else {
                // Regular user login
                new Thread(() -> {
                    User user = userDAO.getUserByUsernameandPassword(username, password);
                    runOnUiThread(() -> {
                        if (user != null) {
                            loginUser(user.getUserId());
                            goToMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });


        // Handle register button click
        registerButton.setOnClickListener(view -> {
            Log.d(TAG, "Register button clicked");
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(LoginActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                Log.d(TAG, "Attempting to register user: " + username);

                User existingUser = userDAO.getUserByUsername(username);
                Log.d(TAG, "Existing user: " + existingUser);

                if (existingUser != null) {
                    Log.d(TAG, "Username already exists: " + username);
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Username already exists", Toast.LENGTH_SHORT).show());

                } else {
                    Log.d(TAG, "Registering new user: " + username);

                    User newUser = new User(username, password);
                    userDAO.insertUser(newUser);
                    User insertedUser = userDAO.getUserByUsername(username);
                    int newUserId = insertedUser.getUserId();

                    // Add the default city
                    CityDAO cityDAO = db.cityDAO();
                    City seasideCity = new City("Seaside", (int)newUserId);
                    cityDAO.insertCity(seasideCity);

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                        switchToLoginMode();
                    });
                }
            }).start();
        });

        // Handle toggle button click to switch between login and registration
        toggleButton.setOnClickListener(view -> {
            if (isRegisterMode) {
                switchToLoginMode();
            } else {
                switchToRegisterMode();
            }
        });

        // Default to login mode
        switchToLoginMode();
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
        return prefs.contains("logged_in_user_id");
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void switchToLoginMode() {
        isRegisterMode = false;
        confirmPasswordInput.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
        toggleButton.setText("Don't have an account? Register");
    }

    private void switchToRegisterMode() {
        isRegisterMode = true;
        confirmPasswordInput.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.VISIBLE);
        toggleButton.setText("Already have an account? Login");
    }

    private void goToAdminDashboard(boolean isAdmin) {
        Toast.makeText(this, "Admin logged in. Navigate to admin dashboard.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this,SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isAdmin",isAdmin);
        startActivity(intent);
        finish();
    }
}
