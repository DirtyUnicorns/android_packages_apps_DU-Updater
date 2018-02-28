/*
 * Copyright (C) 2015-2018 The Dirty Unicorns Project
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

package com.dirtyunicorns.duupdater.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;

import com.dirtyunicorns.duupdater.objects.ServerVersion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class Utils extends Vars {

    private static boolean connected = false;

    private static Process process;
    private static String prop = "";

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

    private static ArrayList<File> files;
    private static ArrayList<ServerVersion> serverVersions;

    static ArrayList<File> getFiles(final String dir, final boolean isDeviceFiles) {
        files = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                JSONParser jsonParser = new JSONParser();
                String path = "device=";
                if (isDeviceFiles) {
                    try {
                        process = new ProcessBuilder("/system/bin/getprop", "ro.build.flavor").redirectErrorStream(true).start();
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line = "";
                        while ((line=br.readLine()) != null){
                            prop = line.replace("du_", "").replace("-userdebug","");
                        }
                        process.destroy();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    device = prop;
                    path += device + "&folder=" + dir;
                    link += device;
                } else {
                    path += "&folder=" + dir;
                }

                try {
                    URI uri = new URI("https", null, "download.dirtyunicorns.com", 443, "/json.php", path, null);

                    JSONObject json = jsonParser.getJSONFromUrl(uri.toASCIIString());
                    try {
                        if (json != null) {
                            JSONArray folders = json.getJSONArray(TAG_MASTER);
                            dirs = new String[folders.length()];
                            for (int i = 0; i < folders.length(); i++) {
                                JSONObject d = folders.getJSONObject(i);
                                File f = new File(Parcel.obtain());
                                f.SetFileName(d.getString("filename"));
                                f.SetFileSize(d.getString("filesize"));
                                f.SetFileLink(d.getString("downloads"));
                                f.SetFileMD5(d.getString("md5"));
                                f.GetFileName();
                                files.add(f);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        Collections.reverse(files);
        return files;
    }

    public static ArrayList<ServerVersion> getServerVersions(final String dir, final boolean isDeviceFiles) {
        serverVersions = new ArrayList<>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                JSONParser jsonParser = new JSONParser();
                String path = "device=";
                if (isDeviceFiles) {
                    try {
                        process = new ProcessBuilder("/system/bin/getprop", "ro.build.flavor").redirectErrorStream(true).start();
                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line = "";
                        while ((line=br.readLine()) != null){
                            prop = line.replace("du_", "").replace("-userdebug","");
                        }
                        process.destroy();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    device = prop;
                    path += device + "&folder=" + dir;
                    link += device;
                } else {
                    path += "&folder=" + dir;
                }

                try {
                    URI uri = new URI("https", null, "download.dirtyunicorns.com", 443, "/json.php", path, null);

                    JSONObject json = jsonParser.getJSONFromUrl(uri.toASCIIString());
                    try {
                        if (json != null) {
                            JSONArray folders = json.getJSONArray(TAG_MASTER);
                            dirs = new String[folders.length()];
                            for (int i = 0; i < folders.length(); i++) {

                                JSONObject d = folders.getJSONObject(i);
                                String link = d.getString("downloads");
                                ServerVersion serverVersion = new ServerVersion();
                                String[] buildInfo = d.getString("filename").replace(".zip", "").split("_");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmm");
                                try {
                                    serverVersion.setBuildDate(dateFormat.parse(buildInfo[3].split("\\.")[0]));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                serverVersion.setAndroidVersion(buildInfo[2]);
                                serverVersion.setBuildType(buildInfo[3].split("\\.")[2]);
                                serverVersion.setMajorVersion(Integer.valueOf(buildInfo[3].split("\\.")[1].replace("v", "")));
                                serverVersion.setMinorVersion(Integer.valueOf(buildInfo[3].split("\\.")[2].split("-")[0]));
                                serverVersion.setLink(link);
                                serverVersions.add(serverVersion);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        Collections.reverse(files);
        return serverVersions;
    }

    public static boolean isOnline(Context ctx) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
}