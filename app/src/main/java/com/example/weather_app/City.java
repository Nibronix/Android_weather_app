package com.example.weather_app;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "cities",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "user_id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("user_id")}
)
public class City {
    @PrimaryKey(autoGenerate = true)
    public int city_id;

    @ColumnInfo(name = "city_name")
    public String cityName;

    @ColumnInfo(name = "user_id")
    public int userId;

    public City(String cityName, int userId) {
        this.cityName = cityName;
        this.userId = userId;
    }
}
