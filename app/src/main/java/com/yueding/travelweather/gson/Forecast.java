package com.yueding.travelweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yueding on 2017/8/7.
 *
 */

public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class More {
        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature {
        public String max;
        public String min;
    }
}
