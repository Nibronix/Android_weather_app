package com.example.weather_app.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import com.example.weather_app.City;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CityDAO {
    @Insert
    void insert(City city);

    @Query("SELECT * FROM cities WHERE user_id = :userId")
    List<City> getCitiesForUser(int userId);
}
