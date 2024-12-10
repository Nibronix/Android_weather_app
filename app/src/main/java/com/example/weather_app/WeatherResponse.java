package com.example.weather_app;

public class WeatherResponse {
    public Current current;
    public Location location;

    public class Current {
        public double temp_c;
        public double wind_kph;
        public int humidity;
        public Condition condition;

        public class Condition {
            public String text;
            public String icon;
        }
    }

    public class Location {
        public String name;
    }

}
