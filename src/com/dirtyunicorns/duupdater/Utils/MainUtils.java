/*
 * Copyright (C) 2015 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dirtyunicorns.duupdater.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mazwoz on 12/18/14.
 */
@SuppressLint("SimpleDateFormat")
public class MainUtils {



    private static String[] dirs;

    private static final String TAG_MASTER = "dev_info";

    private static ConnectivityManager connectivityManager;
    private static boolean connected = false;
    private static boolean DNSGood = false;

    public static String[] getDirs() {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                JSONParser jsonParser = new JSONParser();

                String path = "https://download.dirtyunicorns.com/json.php?device=" + Build.UPDATER;
                Vars.link = "https://download.dirtyunicorns.com/files/" + Build.UPDATER;

                JSONObject json = jsonParser.getJSONFromUrl(path);
                JSONArray folders = null;
                try{
                    if (json != null) {
                        folders = json.getJSONArray(TAG_MASTER);
                        dirs = new String[folders.length()];
                        for (int i = 0; i < folders.length(); i++) {
                            JSONObject d = folders.getJSONObject(i);
                            String id = d.getString("filename");
                            dirs[i] = id;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        return dirs;
    }

    public static String[] getFiles(final String dir) {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                JSONParser jsonParser = new JSONParser();

                String path = "https://download.dirtyunicorns.com/json.php?device=" + Build.UPDATER + "&folder=" + dir;
                JSONObject json = jsonParser.getJSONFromUrl(path);
                JSONArray folders = null;
                try{
                    if (json != null) {
                        folders = json.getJSONArray(TAG_MASTER);
                        dirs = new String[folders.length()];
                        for (int i = 0; i < folders.length(); i++) {
                            JSONObject d = folders.getJSONObject(i);
                            String id = d.getString("filename").replace(".zip","");
                            dirs[i] = id;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        return dirs;
    }

    public static boolean isOnline(Context ctx) {
        try {
            connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    public static Date StringtoDate(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = format.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean CompareDates(Date buildDate, Date updateDate) {
        if (buildDate.before(updateDate)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean CheckDNS(final Context ctx) {

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL url = new URL("http://download.dirtyunicorns.com");
                    InetAddress address = InetAddress.getByName(url.getHost());
                    String temp = address.toString();
                    String IP = temp.substring(temp.indexOf("/")+1,temp.length());
                    if (IP != null) {
                        DNSGood = true;
                    } else {
                        Dialogs.BadDNS(ctx);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

        });
        t.start();

        while (t.isAlive()) {
            try {
                SystemClock.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return DNSGood;
    }
}
