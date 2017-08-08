package com.yueding.travelweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yueding on 2017/8/7.
 *
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
