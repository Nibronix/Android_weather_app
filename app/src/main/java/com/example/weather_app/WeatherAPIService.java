package com.example.weather_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPIService {
    @GET("current.json")
    Call<WeatherResponse> getCurrentWeather(@Query("key") String apiKey, @Query("q") String location);
}
