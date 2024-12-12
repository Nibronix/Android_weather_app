package com.example.weather_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.bumptech.glide.Glide;
import com.example.weather_app.Database.AppDatabase;
import com.example.weather_app.DAOs.CityDAO;
import com.example.weather_app.DAOs.UserDAO;
import android.widget.LinearLayout;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // API key
    private static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";

    private boolean Celsius = false;
    private boolean windKph = false;

    private AppDatabase db;
    private UserDAO userDAO;
    private CityDAO cityDAO;
    private static final String TAG = "Oreo";
    private boolean isNavigating = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        if (!isUserLoggedIn() && !isNavigating) {
            isNavigating = true;
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        userDAO = db.userDAO();
        cityDAO = db.cityDAO();

        // Create test user. Debug only.
        // User user = new User("John", "123");

        int userId = getLoggedInUserId();

        new Thread(() -> {
            User user = userDAO.getUserById(userId);

            if (user != null) {
                List<City> cities = cityDAO.getCitiesForUser(user.getUserId());
                runOnUiThread(() -> {
                    LinearLayout mainLayout = findViewById(R.id.main);

                    for (City city : cities) {
                        addCardForCity(mainLayout, city.cityName);
                    }
                });
            } else {
                runOnUiThread(() -> {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                });
            }
        }).start();
    }

    private void addCardForCity(LinearLayout mainLayout, String cityName) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.card_layout, mainLayout, false);
        mainLayout.addView(cardView);

        // Find the TextView and ImageView elements
        TextView weatherInfo = cardView.findViewById(R.id.weatherInfo);
        ImageView weatherIcon = cardView.findViewById(R.id.weatherIcon);

        // Create retrofit instance to fetch weather data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherAPIService APIService = retrofit.create(WeatherAPIService.class);

        APIService.getCurrentWeather(API_KEY, cityName).enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    // Check Celsius and WindKph bools. If they're false, convert to the alternative (F and mph).
                    @SuppressLint("DefaultLocale") String temp_sign = Celsius ? String.format("%d°C", Math.round(weather.current.temp_c))
                            : String.format("%d°F", Math.round(weather.current.temp_c * 9 / 5 + 32));

                    @SuppressLint("DefaultLocale") String wind_type = windKph ? String.format("%d km/h", Math.round(weather.current.wind_kph))
                            : String.format("%d mph", Math.round(weather.current.wind_kph * 0.621371));

                    // Weather details
                    String weatherDetails =
                            weather.location.name +
                            "\n " + temp_sign +
                            "\nWind: " + wind_type +
                            "\nHumidity: " + weather.current.humidity + "%" +
                            "\n" + weather.current.condition.text;

                    weatherInfo.setText(weatherDetails);


                    if (!isDestroyed() && !isFinishing()) {
                        String iconUrl = "https:" + weather.current.condition.icon;
                        Glide.with(MainActivity.this).load(iconUrl).into(weatherIcon);
                    }
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

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("logged_in_user_id", -1);
        Log.d(TAG, "isUserLoggedIn: userId = " + userId);
        return userId != -1;
    }

    private int getLoggedInUserId() {
        SharedPreferences prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return prefs.getInt("logged_in_user_id", -1);
    }
}