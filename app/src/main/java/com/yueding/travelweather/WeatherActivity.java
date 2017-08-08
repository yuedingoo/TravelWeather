package com.yueding.travelweather;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yueding.travelweather.gson.Forecast;
import com.yueding.travelweather.gson.Weather;
import com.yueding.travelweather.util.HttpUtil;
import com.yueding.travelweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView weatherInfoText;
    private TextView weatherDegreeText;
    private LinearLayout forcastLayout;

    private TextView yaQiangValue;
    private TextView shiDuValue;
    private TextView feelTempValue;
    private TextView airQltValue;
    private TextView pm25Value;
    private TextView pm10Value;

    private TextView comfortText;
    private TextView carWashText;
    private TextView dressText;
    private TextView influenzaText;
    private TextView sportText;
    private TextView travelText;
    private TextView uvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        bindView();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    private void requestWeather(String weatherId) {
        String weatherKey = "&key=b7bd517422ff45d8a393da8845f282c3";
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + weatherKey;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature;
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        weatherInfoText.setText(weatherInfo);
        weatherDegreeText.setText(degree);
        forcastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forcastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            String min = forecast.temperature.min + "°";
            String max = forecast.temperature.max + "°";
            minText.setText(min);
            maxText.setText(max);
            forcastLayout.addView(view);
        }
        yaQiangValue.setText(weather.now.pressure);
        String shidu = weather.now.humidity + "%";
        shiDuValue.setText(shidu);
        String feel = weather.now.feeltemp + "°C";
        feelTempValue.setText(feel);
        String aqi = weather.aqi.city.qlty + " " + weather.aqi.city.aqi;
        airQltValue.setText(aqi);
        pm25Value.setText(weather.aqi.city.pm25);
        pm10Value.setText(weather.aqi.city.pm10);

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String dress = "穿衣指数：" + weather.suggestion.dress.info;
        String influenza = "感冒指数：" + weather.suggestion.influenza.info;
        String sport = "运动指数：" + weather.suggestion.sport.info;
        String travel = "旅游指数：" + weather.suggestion.travel.info;
        String uv = "紫外线指数：" + weather.suggestion.uv.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        dressText.setText(dress);
        influenzaText.setText(influenza);
        sportText.setText(sport);
        travelText.setText(travel);
        uvText.setText(uv);

        weatherLayout.setVisibility(View.VISIBLE);

    }

    private void bindView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        weatherDegreeText = (TextView) findViewById(R.id.weather_degree_text);
        forcastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        yaQiangValue = (TextView) findViewById(R.id.ya_qiang_value);
        shiDuValue = (TextView) findViewById(R.id.shi_du_value);
        feelTempValue = (TextView) findViewById(R.id.feel_temp_value);
        airQltValue = (TextView) findViewById(R.id.air_qlt_value);
        pm25Value = (TextView) findViewById(R.id.pm25_value);
        pm10Value = (TextView) findViewById(R.id.pm10_value);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.carWash_text);
        dressText = (TextView) findViewById(R.id.dress_text);
        influenzaText = (TextView) findViewById(R.id.influenza_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        travelText = (TextView) findViewById(R.id.travel_text);
        uvText = (TextView) findViewById(R.id.uv_text);
    }
}
