package com.example.weather_app;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int user_id;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getUserId() {
        return user_id;

    }

    public String getPassword() {
        return password;
    }

    // Setter for userId (needed if you're inserting a user and want to retrieve the generated ID)
    public void setUserId(int userId) {
        this.user_id = userId;
    }
}



