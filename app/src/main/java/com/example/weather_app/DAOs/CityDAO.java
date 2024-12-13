package com.example.weather_app.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import com.example.weather_app.City;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CityDAO {
    @Query("SELECT * FROM cities WHERE user_id = :userId")
    List<City> getCitiesForUser(int userId);

    @Insert
    void insertCity(City city);

    @Query("DELETE FROM cities WHERE city_name = :cityName AND user_id = :userId")
    void deleteCityByNameAndUserId(String cityName, int userId);

    @Query("SELECT * FROM cities WHERE city_name = :cityName AND user_id = :userId LIMIT 1")
    City getCityByNameAndUserId(String cityName, int userId);
}
