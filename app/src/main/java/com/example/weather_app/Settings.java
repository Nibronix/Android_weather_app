package com.example.weather_app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "settings")
public class Settings {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "is_celsius")
    private boolean isCelsius; // true for Celsius, false for Fahrenheit

    @ColumnInfo(name = "selected_cities")
    private String selectedCities; // Comma-separated list of city names


    public Settings(boolean isCelsius, String selectedCities) {
        this.isCelsius = isCelsius;
        this.selectedCities = selectedCities;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) { // Room requires setter for primary key if autoGenerate is true
        this.id = id;
    }

    public boolean isCelsius() {
        return isCelsius;
    }

    public void setCelsius(boolean celsius) {
        isCelsius = celsius;
    }

    public String getSelectedCities() {
        return selectedCities;
    }

    public void setSelectedCities(String selectedCities) {
        this.selectedCities = selectedCities;
    }
}
