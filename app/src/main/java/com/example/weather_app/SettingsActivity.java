package com.example.weather_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.weather_app.DAOs.CityDAO;
import com.example.weather_app.Database.AppDatabase;
import com.example.weather_app.DAOs.SettingsDAO;

public class SettingsActivity extends AppCompatActivity {

    private Switch temperatureSwitch;
    private Switch windSwitch;
    private EditText cityInput;
    private Button addCityButton, deleteCityButton, saveSettingsButton, logoutButton;
    private SettingsDAO settingsDAO;
    private AppDatabase db;
    private CityDAO cityDAO;
    private Settings currentSettings;
    private int userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userId = getIntent().getIntExtra("userId", -1);

        temperatureSwitch = findViewById(R.id.temperatureSwitch);
        cityInput = findViewById(R.id.cityInput);
        addCityButton = findViewById(R.id.addCityButton);
        deleteCityButton = findViewById(R.id.deleteCityButton);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        logoutButton = findViewById(R.id.logoutButton);
        windSwitch = findViewById(R.id.windSwitch);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        settingsDAO = db.settingsDAO();
        cityDAO = db.cityDAO();


        new Thread(() -> {
            currentSettings = settingsDAO.getCurrentSettings();

            if (currentSettings == null) {
                // Initialize default settings if none exist
                currentSettings = new Settings(false, false, "");
                settingsDAO.insertSettings(currentSettings);
            }
            runOnUiThread(() -> {
                temperatureSwitch.setChecked(currentSettings.isCelsius());
                windSwitch.setChecked(currentSettings.isWindKPH());
            });
        }).start();

        addCityButton.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                int userId = prefs.getInt("logged_in_user_id", -1);

                new Thread(() -> {
                    City existingCity = cityDAO.getCityByNameAndUserId(city, userId);
                    runOnUiThread(() -> {
                        // If city already exists
                        if (existingCity != null) {
                            Toast.makeText(this, "City already exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            // City not in database, proceed to add
                            String updatedCities = currentSettings.getSelectedCities();

                            if (!updatedCities.isEmpty()) {
                                updatedCities += "," + city;
                            } else {
                                updatedCities = city;
                            }

                            currentSettings.setSelectedCities(updatedCities);
                            City newCity = new City(city, userId);

                            new Thread(() -> {
                                cityDAO.insertCity(newCity);
                            }).start();

                            Toast.makeText(this, "City added!", Toast.LENGTH_SHORT).show();
                            cityInput.setText("");
                        }
                    });
                }).start();
            }
        });

        deleteCityButton.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
                int userId = prefs.getInt("logged_in_user_id", -1);

                new Thread(() -> {
                    City existingCity = cityDAO.getCityByNameAndUserId(city, userId);
                    runOnUiThread(() -> {
                        if (existingCity == null) {
                            Toast.makeText(this, "City doesn't exist!", Toast.LENGTH_SHORT).show();
                        } else {
                            // City is in the database, proceed to delete
                            String updatedCities = currentSettings.getSelectedCities().replace(city, "").replace(",,", ",");
                            currentSettings.setSelectedCities(updatedCities);

                            new Thread(() -> {
                                cityDAO.deleteCityByNameAndUserId(city, userId);
                            }).start();

                            Toast.makeText(this, "City deleted!", Toast.LENGTH_SHORT).show();
                            cityInput.setText("");
                        }
                    });
                }).start();
            }
        });

        saveSettingsButton.setOnClickListener(view -> {
            currentSettings.setCelsius(temperatureSwitch.isChecked());
            currentSettings.setWindKPH(windSwitch.isChecked());

            new Thread(() -> settingsDAO.updateSettings(currentSettings)).start();
            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(view -> logoutUser());
    }

    private void logoutUser() {

        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}
