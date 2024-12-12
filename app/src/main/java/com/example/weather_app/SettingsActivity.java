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

import com.example.weather_app.Database.AppDatabase;
import com.example.weather_app.DAOs.SettingsDAO;

public class SettingsActivity extends AppCompatActivity {

    private Switch temperatureSwitch;
    private EditText cityInput;
    private Button addCityButton, deleteCityButton, saveSettingsButton, logoutButton;
    private SettingsDAO settingsDAO;
    private Settings currentSettings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        temperatureSwitch = findViewById(R.id.temperatureSwitch);
        cityInput = findViewById(R.id.cityInput);
        addCityButton = findViewById(R.id.addCityButton);
        deleteCityButton = findViewById(R.id.deleteCityButton);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        logoutButton = findViewById(R.id.logoutButton);


        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        settingsDAO = db.settingsDAO();


        new Thread(() -> {
            currentSettings = settingsDAO.getCurrentSettings();
            if (currentSettings == null) {
                // Initialize default settings if none exist
                currentSettings = new Settings(true, "");
                settingsDAO.insertSettings(currentSettings);
            }
            runOnUiThread(() -> {
                temperatureSwitch.setChecked(currentSettings.isCelsius());
            });
        }).start();

        addCityButton.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                String updatedCities = currentSettings.getSelectedCities();
                if (!updatedCities.isEmpty()) {
                    updatedCities += "," + city;
                } else {
                    updatedCities = city;
                }
                currentSettings.setSelectedCities(updatedCities);
                Toast.makeText(this, "City added!", Toast.LENGTH_SHORT).show();
                cityInput.setText("");
            }
        });


        deleteCityButton.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                String updatedCities = currentSettings.getSelectedCities().replace(city, "").replace(",,", ",");
                currentSettings.setSelectedCities(updatedCities);
                Toast.makeText(this, "City deleted!", Toast.LENGTH_SHORT).show();
                cityInput.setText("");
            }
        });


        saveSettingsButton.setOnClickListener(view -> {
            currentSettings.setCelsius(temperatureSwitch.isChecked());
            new Thread(() -> settingsDAO.updateSettings(currentSettings)).start();
            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
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
