package com.dirtyunicorns.duupdater.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dirtyunicorns.duupdater.services.CheckService;

/**
 * Created by mazwoz on 10/13/16.
 */

public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, CheckService.class);
        context.startService(service);
    }
}