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

    private ArrayList<File> files = new ArrayList<File>();

    public ArrayList<File> getFiles(final String dir) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                JSONParser jsonParser = new JSONParser();

                device = Build.BOARD;

                path += device + "&folder=" + dir;
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
        /*for (File f : files) {
            System.out.println(f.GetFileName());
        }*/
        return files;
    }
}
