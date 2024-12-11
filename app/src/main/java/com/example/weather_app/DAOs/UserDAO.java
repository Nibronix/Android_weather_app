package com.example.weather_app.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.weather_app.User;

@Dao
public interface UserDAO {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    User getUserById(int userId);


}
