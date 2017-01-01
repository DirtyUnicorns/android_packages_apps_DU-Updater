package com.dirtyunicorns.duupdater.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.objects.CurrentVersion;
import com.dirtyunicorns.duupdater.objects.ServerVersion;
import com.dirtyunicorns.duupdater.utils.NetUtils;
import com.dirtyunicorns.duupdater.utils.ServerUtils;

import java.util.ArrayList;

/**
 * Created by mazwoz on 10/12/16.
 */

public class CheckService extends Service{

    private ArrayList<ServerVersion> serverVersions;
    private final int MAJOR_VERSION = 1;
    private final int MINOR_VERSION = 2;
    private final int BUILD_DATE = 3;
    private NotificationManager mNotifyManager;
    private CurrentVersion currentVersion;
    private Intent downloadIntent;
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
                serverVersions = ServerUtils.getServerVersions(getBuildString(), true);
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
        } else if (currentVersion.isRc()) {
            return "Rc";
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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_warning);
        mBuilder.setContentTitle(getString(R.string.dialog_title));
        downloadIntent.putExtra("fileName", link.split("/")[link.split("/").length - 1]);
        downloadIntent.putExtra("url", link);
        PendingIntent startDownload = PendingIntent.getService(this, 42, downloadIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.addAction(android.R.drawable.stat_sys_download_done, getString(R.string.dialog_download), startDownload);

        switch (UPDATE_TYPE) {
            case MAJOR_VERSION:
                mBuilder.setContentText("DU " + info + ") " + getString(R.string.major_version_text));
                break;
            case MINOR_VERSION:
                mBuilder.setContentText(getString(R.string.minor_version_text) + " (DU " + info + ") " + getString(R.string.major_version_text));
                break;
            case BUILD_DATE:
                mBuilder.setContentText(getString(R.string.build_date_text));
        }
        mNotifyManager.notify(00, mBuilder.build());
    }
}
