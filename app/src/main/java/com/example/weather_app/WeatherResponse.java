package com.example.weather_app;

public class WeatherResponse {
    public Current current;
    public Location location;

    public class Current {
        public double temp_c;
        public double wind_kph;
        public int humidity;

        public static class Condition {
            public static String conditionText;
            public static String icon;
        }
    }

    public class Location {
        public String name;
    }

}
