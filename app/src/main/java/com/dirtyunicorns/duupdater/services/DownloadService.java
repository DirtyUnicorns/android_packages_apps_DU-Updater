package com.dirtyunicorns.duupdater.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.utils.Utils;

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
    private static final String STOPTEXT = "STOPDOWNLOAD";
    private Context ctx;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private long total;
    private DownloadFilesTasks downloadFilesTasks;
    private Intent stopIntent;
    private PendingIntent stopDownload;

    public class LocalBinder extends Binder {
        DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ctx = this;

        stopIntent = new Intent(this, DownloadService.class);
        stopIntent.setAction(STOPTEXT);
        stopDownload = PendingIntent.getService(this, 42, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        final String fileName = intent.getStringExtra("fileName");

        if (intent.getAction() != null && intent.getAction().equals(STOPTEXT)) {
            CancelDownload();
            return START_NOT_STICKY;
        }

        if (fileName == null) throw new AssertionError();
        final String urlToDownload = intent.getStringExtra("url");
        if (urlToDownload == null) throw new AssertionError();
        mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.cancel), stopDownload);
        downloadFilesTasks = new DownloadFilesTasks();
        downloadFilesTasks.execute(fileName, urlToDownload);

        return START_NOT_STICKY;
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

    public void CancelDownload() {
        try {
            if (downloadFilesTasks.getStatus() == AsyncTask.Status.RUNNING) {
                downloadFilesTasks.cancel(true);
                while (!downloadFilesTasks.isCancelled()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                mBuilder.setContentText(getString(R.string.download_cancelled));
                mBuilder.setContentTitle(getString(R.string.du_download));
                mBuilder.setProgress(100, 0, false);
                mBuilder.mActions.clear();
                mBuilder.setLights(Color.RED, 1500, 1500);
                mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DownloadStats {
        int prog;
        double speed;
    }

    private class DownloadFilesTasks extends AsyncTask<String, DownloadStats, Long> {

        private volatile boolean running = true;

        @Override
        protected void onCancelled() {
            running = true;
        }

        @Override
        protected Long doInBackground(String... params) {

            try {
                DownloadStats downloadStats = new DownloadStats();
                final String fileName = params[0];
                final String urlToDownload = params[1];
                URL url = new URL(urlToDownload);
                URLConnection connection = url.openConnection();
                connection.connect();

                boolean isRunning = true;
                mBuilder.setContentTitle(fileName);
                final int fileLength = connection.getContentLength();

                InputStream input = new BufferedInputStream(connection.getInputStream());
                BufferedInputStream bis = new BufferedInputStream(input);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + fileName);
                while (isRunning) {

                    byte data[] = new byte[1024];
                    total = 0;
                    long startTime = System.currentTimeMillis();
                    long progressInterval = System.currentTimeMillis();
                    int count;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    while (isRunning && (count = bis.read(data)) > -1) {
                        total += count;
                        downloadStats.prog = (int) (total * 100 / fileLength);
                        output.write(data, 0, count);
                        long estimatedTime = (System.currentTimeMillis() - startTime);
                        long updateInterval = (System.currentTimeMillis() - progressInterval);
                        downloadStats.speed = (total / estimatedTime);
                        if (downloadStats.prog < 100 && updateInterval >= NOTIFICATION_UPDATE_INTERVAL) {
                            progressInterval = System.currentTimeMillis();
                            final DownloadStats tmp = new DownloadStats();
                            tmp.prog = downloadStats.prog;
                            tmp.speed = downloadStats.speed;
                            output.flush();
                            publishProgress(tmp);
                        } else if (downloadStats.prog == 100){
                            isRunning = false;
                        }
                    }
                    mBuilder.setProgress(100, 100, false);
                    mBuilder.mActions.clear();
                    mBuilder.setContentText(getString(R.string.download_happy));
                    mBuilder.setLights(Color.RED, 1500, 1500);
                    mBuilder.setOnlyAlertOnce(true);
                    mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                    mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
                    bis.close();
                    System.out.println("this should be closed");
                    output.close();
                    input.close();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                onError();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                onError();
            } catch (IOException e) {
                e.printStackTrace();
                onError();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(DownloadStats... stats) {
            final int prog = stats[0].prog;
            final double speed = stats[0].speed;
            mBuilder.setProgress(100, prog, false);
            mBuilder.setContentText(Utils.ConvertSpeed(speed) + "          " + prog + "% " + getString(R.string.complete));
            mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
        }
    }

    private void onError() {
        mBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
        mBuilder.setContentText(getString(R.string.download_failed));
        mBuilder.setContentTitle(getString(R.string.du_download));
        mBuilder.setProgress(100, 0, false);
        mBuilder.mActions.clear();
        mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
    }
}
