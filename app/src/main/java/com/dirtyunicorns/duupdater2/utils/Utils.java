package com.dirtyunicorns.duupdater2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by mazwoz on 7/5/16.
 */
public class Utils extends Vars{

    protected static ConnectivityManager connectivityManager;
    protected static boolean connected = false;

    public static String ConvertSpeed(double currentSpeed) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (currentSpeed > 1024) {
            return df.format(currentSpeed/1024) + "MB/s";
        } else if ( currentSpeed < 0) {
            return "Stalled download, please wait";
        } else {
            df = new DecimalFormat("0");
            return df.format(currentSpeed) + "KB/s";
        }
    }

}
