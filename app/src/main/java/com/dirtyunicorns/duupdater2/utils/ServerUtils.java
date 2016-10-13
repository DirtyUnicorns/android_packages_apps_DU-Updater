package com.dirtyunicorns.duupdater2.utils;

import android.os.Build;
import android.os.Parcel;
import android.os.SystemClock;

import com.dirtyunicorns.duupdater2.objects.ServerVersion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mazwoz on 7/5/16.
 */
public class ServerUtils extends Utils {

    private ArrayList<File> files;
    private ArrayList<ServerVersion> serverVersions;
    private String dir;
    private boolean isDevFiles;
    private SimpleDateFormat dateFormat;

    public ServerUtils() {
        files = new ArrayList<File>();
    }

    public ArrayList<File> getFiles(String dirP, boolean isDeviceFiles) {
        isDevFiles = isDeviceFiles;
        dir = dirP;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                JSONParser jsonParser = new JSONParser();
                String path = "device=";
                if (isDevFiles) {
                    device = Build.BOARD;
                    path += device + "&folder=" + dir;
                    link += device;
                } else {
                    path += "&folder=" + dir;
                }

                try {
                    URI uri = new URI("https", null, "download.dirtyunicorns.com", 443, "/json.php", path, null);

                    JSONObject json = jsonParser.getJSONFromUrl(uri.toASCIIString());
                    JSONArray folders = null;
                    try {
                        if (json != null) {
                            folders = json.getJSONArray(TAG_MASTER);
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

    public ArrayList<ServerVersion> getServerVersions(String dirP, boolean isDeviceFiles) {
        serverVersions = new ArrayList<>();
        isDevFiles = isDeviceFiles;
        dir = dirP;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                JSONParser jsonParser = new JSONParser();
                String path = "device=";
                if (isDevFiles) {
                    device = Build.BOARD;
                    path += device + "&folder=" + dir;
                    link += device;
                } else {
                    path += "&folder=" + dir;
                }

                try {
                    URI uri = new URI("https", null, "download.dirtyunicorns.com", 443, "/json.php", path, null);

                    JSONObject json = jsonParser.getJSONFromUrl(uri.toASCIIString());
                    JSONArray folders = null;
                    try{
                        if (json != null) {
                            folders = json.getJSONArray(TAG_MASTER);
                            dirs = new String[folders.length()];
                            for (int i = 0; i < folders.length(); i++) {

                                JSONObject d = folders.getJSONObject(i);
                                String link = d.getString("downloads");
                                ServerVersion serverVersion = new ServerVersion();
                                String[] buildInfo = d.getString("filename").replace(".zip","").split("_");
                                dateFormat = new SimpleDateFormat("yyyyMMdd-hhmm");
                                try {
                                    serverVersion.setBuildDate(dateFormat.parse(buildInfo[3].split("\\.")[0]));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                serverVersion.setAndroidVersion(buildInfo[2]);
                                serverVersion.setBuildType(buildInfo[3].split("\\.")[2]);
                                serverVersion.setMajorVersion(Integer.valueOf(buildInfo[3].split("\\.")[1].replace("v","")));
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
}
