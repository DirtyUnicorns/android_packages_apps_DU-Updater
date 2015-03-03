/*
* Copyright (C) 2015 Dirty Unicorns
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.dirtyunicorns.duupdater.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.dirtyunicorns.duupdater.Utils.MainUtils;

import java.util.Date;

/**
 * Created by mazwoz on 03.03.15.
 */
public class UpdateCheck extends IntentService{

    public UpdateCheck() {
        super("UpdateCheck");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String buildType = BuildType().toLowerCase();
        Date[] potUpdates;
        String[] strPotUpdates;
        int i = 0;

        if (!buildType.equals("unofficial")) {

            String[] potentialUpdates = MainUtils.getFiles(buildType.substring(0,1).toUpperCase() + buildType.substring(1));

            if (potentialUpdates.length > 0) {
                potUpdates = new Date[potentialUpdates.length];
                strPotUpdates = new String[potentialUpdates.length];
                for (String update : potentialUpdates) {
                    Date buildDate = MainUtils.StringtoDate(BuildDate());
                    Date updateDate = MainUtils.StringtoDate(GetDateFromUpdate(update));

                    if (MainUtils.CompareDates(buildDate,updateDate)) {
                        potUpdates[i] = updateDate;
                        strPotUpdates[i] = update;
                        i++;
                    }
                }

                Intent downloadIntent = new Intent(getApplication(), DownloadIntent.class);
                downloadIntent.putExtra("fileName", strPotUpdates[i - 1]);
                downloadIntent.putExtra("dirName", buildType.substring(0,1).toUpperCase() + buildType.substring(1));
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, downloadIntent, 0);

                Notification mBuilder = new Notification.Builder(getApplication())
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentTitle("A new update is available")
                        .setContentText("Would you like to download it now?")
                        .setAutoCancel(true)
                        .addAction(android.R.drawable.stat_sys_download, "Download", pendingIntent).build();
                mBuilder.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;

                NotificationManager mNotificationManaager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManaager.notify(0, mBuilder);
            }

        }
    }

    private String GetDateFromUpdate(String fileName) {
        String[] splitUnders = fileName.split("_");
        String[] splitDash = splitUnders[3].split("-");
        return splitDash[0];
    }


    private String BuildType() {
        String buildType = Build.VERSION.CODENAME;
        return  buildType;
    }

    private String BuildDate() {
        String buildDate = Build.DISPLAY;
        buildDate = buildDate.split("\\.")[4];
        return buildDate;
    }
}
