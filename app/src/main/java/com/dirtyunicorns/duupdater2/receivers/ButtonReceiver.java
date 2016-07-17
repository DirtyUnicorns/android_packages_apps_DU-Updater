package com.dirtyunicorns.duupdater2.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dirtyunicorns.duupdater2.services.DownloadService;

/**
 * Created by mazwoz on 7/17/16.
 */
public class ButtonReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationID = intent.getIntExtra("notificationID", 0);

        System.out.println("We got here");

        DownloadService downloadService = new DownloadService();
        downloadService.CancelDownload();
        Intent service = new Intent(context, DownloadService.class);
        context.stopService(service);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationID);
    }
}
