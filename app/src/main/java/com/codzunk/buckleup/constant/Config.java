package com.codzunk.buckleup.constant;

import android.Manifest;

public class Config {
    public static final int PERMISSIONS_REQUEST = 1;
    public static final String ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713" ;
    public static final String ADMOB_INTERESTITIAL_ID = "" ;
    public static final String NATIVE_ADS_ID = "";
    public static final String REWARD_AD_ID = "ca-app-pub-3940256099942544/5224354917";
    public static String[] PERMISSIONS_REQUIRED = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final String STATUS_INTENT = "com.codzunk.buckleup";

    public static final int NOTIFICATION_ID = 1;
    public static final int FOREGROUND_SERVICE_ID = 1;
    public static final int CONFIG_CACHE_EXPIRY = 600;  // 10 minutes.

    public static long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    public static long FASTEST_INTERVAL = 2000; /* 2 sec */

}
