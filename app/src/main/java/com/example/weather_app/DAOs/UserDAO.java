package com.example.weather_app.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.weather_app.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM users WHERE user_id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User getUserByUsernameandPassword(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

}
