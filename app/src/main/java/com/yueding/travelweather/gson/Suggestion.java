package com.yueding.travelweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yueding on 2017/8/7.
 *
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("drsg")
    public Dress dress;

    @SerializedName("flu")
    public Influenza influenza;

    public Sport sport;

    @SerializedName("trav")
    public Travel travel;

    public UV uv;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Dress {
        @SerializedName("txt")
        public String info;
    }

    public class Influenza {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

    public class Travel {
        @SerializedName("txt")
        public String info;
    }

    public class UV {
        @SerializedName("txt")
        public String info;
    }
}
