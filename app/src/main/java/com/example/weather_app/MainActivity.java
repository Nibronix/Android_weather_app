package com.example.weather_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    // API key
    private static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String ICON_BASE_URL = "https://cdn.weatherapi.com/weather/64x64/";

    private boolean Celsius = false;
    private boolean windKph = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView weatherInfo = findViewById(R.id.weatherInfo);
        ImageView weatherIcon = findViewById(R.id.weatherIcon);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherAPIService APIService = retrofit.create(WeatherAPIService.class);

        String defaultLocation = "Los Angeles, CA";

        APIService.getCurrentWeather(API_KEY, defaultLocation).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    // Check Celsius and WindKph bools. If they're false, convert to the alternative (F and mph).
                    String temp_sign = Celsius ? String.format("%d°C", Math.round(weather.current.temp_c))
                            : String.format("%d°F", Math.round(weather.current.temp_c * 9 / 5 + 32));

                    String wind_type = windKph ? String.format("%d km/h", Math.round(weather.current.wind_kph))
                            : String.format("%d mph", Math.round(weather.current.wind_kph * 0.621371));


                    String weatherDetails =
                            weather.location.name +
                            "\n " + temp_sign +
                            "\nWind: " + wind_type +
                            "\nHumidity: " + weather.current.humidity + "%" +
                            "\n" + weather.current.condition.text;

                    weatherInfo.setText(weatherDetails);

                    // Load weather icon
                    String iconUrl = weather.current.condition.icon;
                    Glide.with(MainActivity.this).load(iconUrl).into(weatherIcon);
                } else {
                    weatherInfo.setText("Can't fetch data...");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherInfo.setText(t.getMessage());
            }
        });

    }
}