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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

public class Utils extends Vars {

    private static boolean connected = false;

    private static ArrayList<File> files;

    static ArrayList<File> getFiles(final String dir, final boolean isDeviceFiles) {
        files = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONParser jsonParser = new JSONParser();
                String path = "files/";
                if (isDeviceFiles) {
                    device = readProp("ro.build.flavor", true);
                    path += device + "/" + dir;
                    device = "/" + device;
                } else {
                    path += dir;
                    device = "";
                }

                JSONArray json = jsonParser.getJSONFromUrl(path);
                try {
                    if (json != null) {
                        dirs = new String[json.length()];
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject jsonObject = json.getJSONObject(i);
                            File f = new File(Parcel.obtain());
                            String fileName = jsonObject.getString("filename");
                            f.SetFileName(fileName);
                            f.SetFileSize(jsonObject.getString("filesize"));
                            f.SetFileMD5(jsonObject.getString("fileMd5"));
                            f.SetFileLink("https://download.dirtyunicorns.com/api/download" +
                                    device + "/" + dir + "/" + fileName);
                            files.add(f);
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
        Collections.reverse(files);
        return files;
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

    public static String readProp(String propName, Boolean device) {
        Process process = null;
        BufferedReader bufferedReader = null;

        try {
            process = new ProcessBuilder().command("/system/bin/getprop", propName).redirectErrorStream(true).start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            if (line == null){
                return "";
            }
            if (device) {
                return line.replace("du_", "").replace("-userdebug","").replace("-user","");
            } else {
                return line;
            }
        } catch (Exception e) {
            Log.e(TAG,"Failed to read System Property " + propName,e);
            return "";
        } finally{
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(TAG,"Failed to read System Property " + propName,e);
                }
            }
            if (process != null){
                process.destroy();
            }
        }
    }
}