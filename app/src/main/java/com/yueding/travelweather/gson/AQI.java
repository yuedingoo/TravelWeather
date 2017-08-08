package com.yueding.travelweather.gson;

/**
 * Created by yueding on 2017/8/7.
 *
 */

public class AQI {

    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm10;
        public String pm25;
        public String qlty;
    }
}
