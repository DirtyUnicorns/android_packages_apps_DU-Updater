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

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.Utils.Download;
import com.dirtyunicorns.duupdater.Utils.Vars;

public class DownloadIntent extends Activity {

    private String dir;
    private String file;
    private String url;
    private Context activity;
    private ProgressDialog mProgressDialog;
    public static Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_intent);

        thisActivity = this;

        Intent i = getIntent();
        if (i.getExtras() != null) {
            file = i.getStringExtra("fileName");
            dir = i.getStringExtra("dirName");
        }

        activity = this;

        url = Vars.link + "/" + dir + "/" + file + ".zip";

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // instantiate it within the onCreate method
                        mProgressDialog = new ProgressDialog(activity);
                        mProgressDialog.setMessage("Downloading \n\n" + file + " \n\nto /sdcard/Download");
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mProgressDialog.setCancelable(false);
                        // execute this when the downloader must be fired
                        final Download downloadTask = new Download(activity, file, mProgressDialog);
                        downloadTask.execute(url);

                        NotificationManager mNotificationManaager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManaager.cancel(0);
                    }
                });
            }
        });
        t.start();

    }
}
