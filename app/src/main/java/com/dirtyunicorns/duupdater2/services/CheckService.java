package com.dirtyunicorns.duupdater2.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.dirtyunicorns.duupdater2.objects.CurrentVersion;
import com.dirtyunicorns.duupdater2.objects.ServerVersion;
import com.dirtyunicorns.duupdater2.utils.NetUtils;
import com.dirtyunicorns.duupdater2.utils.ServerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mazwoz on 10/12/16.
 */

public class CheckService extends Service{

    private ArrayList<ServerVersion> serverVersions;
    private ServerUtils su;
    private final int MAJOR_VERSION = 1;
    private final int MINOR_VERSION = 2;
    private final int BUILD_DATE = 3;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private CurrentVersion currentVersion;
    private Intent downloadIntent;
    private PendingIntent startDownload;
    private static final String STARTTEXT = "STARTDOWNLOAD";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        downloadIntent = new Intent(this, DownloadService.class);
        downloadIntent.setAction(STARTTEXT);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        if (intent.getAction() != null && intent.getAction().equals(STARTTEXT)) {
            mNotifyManager.cancel(00);
            return START_NOT_STICKY;
        } else {
            currentVersion = new CurrentVersion();
            currentVersion.GetInfo();
            if (NetUtils.isOnline(this)) {
                su = new ServerUtils();
                serverVersions = su.getServerVersions(getBuildString(), true);
                if (serverVersions != null) {
                    ParseBuilds();
                }
            }
            return START_NOT_STICKY;
        }
    }

    private String getBuildString() {
        if (currentVersion.isOfficial()) {
            return "Official";
        } else if (currentVersion.isWeekly()) {
            return "Weeklies";
        } else if (currentVersion.isTest()) {
            return "Test";
        } else {
            return "Official";
        }

    }

    private void ParseBuilds() {
        for (ServerVersion serverVersion : serverVersions) {
            serverVersion.getLink();
            if (currentVersion.getMajorVersion() < serverVersion.getMajorVersion()) {
                UpdateNotification(MAJOR_VERSION, String.valueOf(serverVersion.getMajorVersion()), serverVersion.getLink());

                break;
            } else if (currentVersion.getMajorVersion() == serverVersion.getMajorVersion()) {
                if (currentVersion.getMinorVersion() < serverVersion.getMinorVersion()) {
                    UpdateNotification(MINOR_VERSION, serverVersion.getMajorVersion() + "." + serverVersion.getMinorVersion(), serverVersion.getLink());

                    break;
                } else if (currentVersion.getBuildDate().before(serverVersion.getBuildDate())) {
                    UpdateNotification(BUILD_DATE, null, serverVersion.getLink());

                    break;
                }
            }
        }
    }

    private void UpdateNotification(int UPDATE_TYPE, String info, String link) {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_warning);
        mBuilder.setSubText("Would you like to download it?");
        mBuilder.setContentTitle("DU Update Available");
        downloadIntent.putExtra("fileName", link.split("/")[link.split("/").length - 1]);
        downloadIntent.putExtra("url", link);
        startDownload = PendingIntent.getService(this, 42, downloadIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.addAction(android.R.drawable.stat_sys_download_done,"Download",startDownload);

        switch (UPDATE_TYPE) {
            case MAJOR_VERSION:
                mBuilder.setContentText("DU " + info + " is available for download");
                break;
            case MINOR_VERSION:
                mBuilder.setContentText("An update (DU " + info + ") is available for download");
                break;
            case BUILD_DATE:
                mBuilder.setContentText("A newer build of DU is available for download");
        }

        mNotifyManager.notify(00, mBuilder.build());

    }
}
