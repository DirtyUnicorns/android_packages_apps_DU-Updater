package com.dirtyunicorns.duupdater2.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.NotificationCompat;

import com.dirtyunicorns.duupdater2.MainActivity;

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
    private Context ctx;
    private Intent intent;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private int prog;

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
        this.intent = intent;
        DownloadFilesTasks downloadFilesTasks = new DownloadFilesTasks();
        downloadFilesTasks.execute();
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


    private class DownloadFilesTasks extends AsyncTask<String, String, Long> {

        @Override
        protected Long doInBackground(String... params) {

            try {
                final String fileName = intent.getStringExtra("fileName");
                final String urlToDownload = intent.getStringExtra("url");
                mBuilder = new NotificationCompat.Builder(ctx);
                mBuilder.setContentTitle("Dirty Unicorns")
                        .setContentText("Downloading File")
                        .setSmallIcon(android.R.drawable.stat_sys_download);
                URL url = new URL(urlToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                InputStream input = new BufferedInputStream(connection.getInputStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + fileName);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    prog = (int) (total * 100/fileLength);
                    output.write(data, 0, count);
                    if (prog < 100) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                mBuilder.setProgress(100, prog, false);
                                mBuilder.setContentTitle(fileName);
                                mBuilder.setContentText(prog + "/100");
                                mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

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
    }
}
