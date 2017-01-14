package com.dirtyunicorns.duupdater.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcel;
import android.os.SystemClock;
import android.util.Log;

import com.dirtyunicorns.duupdater.objects.ServerVersion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

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

    private static ArrayList<File> files;
    private static ArrayList<ServerVersion> serverVersions;

    public static ArrayList<File> getFiles(final String dir, final boolean isDeviceFiles) {
        files = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                JSONParser jsonParser = new JSONParser();
                String path = "device=";
                if (isDeviceFiles) {
                    device = Build.BOARD;
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
                    device = Build.BOARD;
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
}
