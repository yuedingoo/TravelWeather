package com.yueding.travelweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yueding on 2017/8/7.
 *
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("fl")
    public String feeltemp;

    @SerializedName("hum")
    public String humidity; //相当湿度

    @SerializedName("pres")
    public String pressure; //大气压

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }

}
