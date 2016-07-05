package com.dirtyunicorns.duupdater2.utils;

import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mazwoz on 7/5/16.
 */
public class ServerUtils extends Utils {

    public static ArrayList<File> getFiles(final String dir) {

        final ArrayList<File> files = new ArrayList<>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                JSONParser jsonParser = new JSONParser();

                device = Build.BOARD;

                path += device + "/" + dir;
                link += device;

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
        return files;
    }
}
