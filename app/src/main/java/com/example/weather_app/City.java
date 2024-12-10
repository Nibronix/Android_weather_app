package com.example.weather_app;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "cities"
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("user_id")
)
public class City {
    @PrimaryKey(autoGenerate = true)
    public int city_id;

    //TODO: Add cityname and userid
}
