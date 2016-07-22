package com.dirtyunicorns.duupdater2.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.dirtyunicorns.duupdater2.utils.Utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mazwoz on 7/6/16.
 */
public class DownloadService extends Service {

    public static final int UPDATE_PROGRESS = 8344;
    public static final long NOTIFICATION_UPDATE_INTERVAL = 1000;
    private Context ctx;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private long total;

    public class LocalBinder extends Binder {
        DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ctx = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        final String fileName = intent.getStringExtra("fileName");
        if (fileName == null) throw new AssertionError();
        final String urlToDownload = intent.getStringExtra("url");
        if (urlToDownload == null) throw new AssertionError();
        mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        DownloadFilesTasks downloadFilesTasks = new DownloadFilesTasks();
        downloadFilesTasks.execute(fileName, urlToDownload);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mNotifyManager.cancel(UPDATE_PROGRESS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public final IBinder mBinder = new LocalBinder();

    public void CancelDownload() {
        DownloadFilesTasks downloadFilesTasks = new DownloadFilesTasks();
        downloadFilesTasks.cancel(true);
    }

    private static class DownloadStats {
        int prog;
        double speed;
    }

    private class DownloadFilesTasks extends AsyncTask<String, DownloadStats, Long> {

        @Override
        protected Long doInBackground(String... params) {

            try {
                final String fileName = params[0];
                final String urlToDownload = params[1];
                URL url = new URL(urlToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();

                mBuilder.setContentTitle(fileName);
                final int fileLength = connection.getContentLength();

                DownloadStats downloadStats = new DownloadStats();
                InputStream input = new BufferedInputStream(connection.getInputStream());
                BufferedInputStream bis = new BufferedInputStream(input);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + fileName);

                byte data[] = new byte[1024];
                total = 0;
                int count;
                long startTime = System.currentTimeMillis();
                long progressInterval = System.currentTimeMillis();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while ((count = bis.read(data)) != -1) {
                    total += count;
                    downloadStats.prog = (int) (total * 100/fileLength);
                    output.write(data, 0, count);
                    long estimatedTime = (System.currentTimeMillis() - startTime);
                    long updateInterval = (System.currentTimeMillis() - progressInterval);
                    downloadStats.speed = (total / estimatedTime);
                    if (downloadStats.prog < 100 && updateInterval >= NOTIFICATION_UPDATE_INTERVAL) {
                        progressInterval = System.currentTimeMillis();
                        final DownloadStats tmp = new DownloadStats();
                        tmp.prog = downloadStats.prog;
                        tmp.speed = downloadStats.speed;
                        publishProgress(tmp);
                    }
                }
                bis.close();
                output.flush();
                output.close();
                input.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(DownloadStats... stats) {
            final int prog = stats[0].prog;
            final double speed = stats[0].speed;
            mBuilder.setProgress(100, prog, false);
            mBuilder.setContentText(Utils.ConvertSpeed(speed) + "          " + prog + "% Complete");
            mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
        }
    }
}