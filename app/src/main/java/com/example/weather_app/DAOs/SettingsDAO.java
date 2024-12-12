package com.example.weather_app.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.weather_app.Settings;

import java.util.List;

@Dao
public interface SettingsDAO {

    @Insert
    void insertSettings(Settings settings);

    @Update
    void updateSettings(Settings settings);

    @Delete
    void deleteSettings(Settings settings);

    @Query("SELECT * FROM settings WHERE id = :id LIMIT 1")
    Settings getSettingsById(int id);

    @Query("SELECT * FROM settings LIMIT 1")
    Settings getCurrentSettings();

    @Query("SELECT * FROM settings")
    List<Settings> getAllSettings();
}