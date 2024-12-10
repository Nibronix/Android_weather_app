package com.example.weather_app.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.weather_app.City;
import com.example.weather_app.DAOs.CityDAO;
import com.example.weather_app.DAOs.UserDAO;
import com.example.weather_app.User;

@Database(entities = {User.class, City.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract CityDAO cityDAO();
}