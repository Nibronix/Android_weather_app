package com.example.weather_app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "settings")
public class Settings {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "is_celsius")
    private boolean isCelsius;
    private boolean windKPH;

    @ColumnInfo(name = "selected_cities")
    private String selectedCities; // Comma-separated list of city names


    public Settings(boolean isCelsius, boolean windKPH, String selectedCities) {
        this.isCelsius = isCelsius;
        this.windKPH = windKPH;
        this.selectedCities = selectedCities;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCelsius() {
        return isCelsius;
    }

    public void setCelsius(boolean celsius) {
        isCelsius = celsius;
    }

    public boolean isWindKPH() {
        return windKPH;
    }

    public void setWindKPH(boolean windKPH) {
        this.windKPH = windKPH;
    }

    public String getSelectedCities() {
        return selectedCities;
    }

    public void setSelectedCities(String selectedCities) {
        this.selectedCities = selectedCities;
    }
}
