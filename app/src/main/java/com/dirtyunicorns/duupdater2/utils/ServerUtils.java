package com.dirtyunicorns.duupdater2.utils;

import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mazwoz on 7/5/16.
 */
public class ServerUtils extends Utils {

    private ArrayList<File> files;
    private String dir;
    private boolean isDevFiles;

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
                String path = "http://download.dirtyunicorns.com/json.php?device=";

                if (isDevFiles) {
                    device = Build.BOARD;
                    path += device + "&folder=" + dir;
                    link += device;
                } else {
                    path += "&folder=" + dir;
                }

                System.out.println(path);

                JSONObject json = jsonParser.getJSONFromUrl(path);
                JSONArray folders = null;
                try{
                    if (json != null) {
                        folders = json.getJSONArray(TAG_MASTER);
                        dirs = new String[folders.length()];
                        for (int i = 0; i < folders.length(); i++) {
                            JSONObject d = folders.getJSONObject(i);
                            File f = new File();
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
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        Collections.reverse(files);
        return files;
    }
}
