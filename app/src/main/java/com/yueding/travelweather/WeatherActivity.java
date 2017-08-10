package com.yueding.travelweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yueding.travelweather.gson.Forecast;
import com.yueding.travelweather.gson.Images;
import com.yueding.travelweather.gson.Weather;
import com.yueding.travelweather.service.AutoUpdateService;
import com.yueding.travelweather.util.HttpUtil;
import com.yueding.travelweather.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

    public DrawerLayout drawerLayout;
    private Button navButton;

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

    public boolean isLocation;
    public String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        bindView();

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        Date currDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateStr = dateFormat.format(currDate);
//        Toast.makeText(WeatherActivity.this, dateStr, Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather", null);

        isLocation = getIntent().getBooleanExtra("isLocate", false);
        if (isLocation) {
            location = getIntent().getStringExtra("weather_id");
        }
        if ((weatherString != null) && !isLocation) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            String updateDate = weather.basic.update.updateTime.split(" ")[0];
            /**判断更新日期，如果更新日期不是当天，则立即更新，否则直接显示*/
            if (!dateStr.equals(updateDate)) {
                requestWeather(mWeatherId);
            } else {
                showWeatherInfo(weather);
            }
        } else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    private void loadBingPic() {
        String requestBingPic = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Images images = Utility.handleImagesResponse(responseText);
                final String bingPicUrl;
                if (images != null) {
                    bingPicUrl = "http://cn.bing.com" + images.url;
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic", bingPicUrl);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(bingPicUrl).into(bingPicImg);
                        }
                    });
                }

            }
        });
    }

    public void requestWeather(String weatherId) {
        String weatherKey = "&key=b7bd517422ff45d8a393da8845f282c3";
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + weatherKey;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "刷新天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
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
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
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
        /**有些城市不能获取AQI信息（香港 澳门 等），会导致程序崩溃 */
        if (weather.aqi != null) {
            String aqi = weather.aqi.city.qlty + " " + weather.aqi.city.aqi;
            airQltValue.setText(aqi);
            pm25Value.setText(weather.aqi.city.pm25);
            pm10Value.setText(weather.aqi.city.pm10);
        } else {
            airQltValue.setText("无");
            pm10Value.setText("无");
            pm25Value.setText("无");
        }

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

        /**启动自动更新服务*/
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

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
        bingPicImg = (ImageView) findViewById(R.id.bing_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);
        navButton = (Button) findViewById(R.id.nav_button);
    }
}
