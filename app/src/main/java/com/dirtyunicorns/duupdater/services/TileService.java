package com.dirtyunicorns.duupdater.services;

import android.content.Intent;

import com.dirtyunicorns.duupdater.LauncherActivity;
import com.dirtyunicorns.duupdater.MainActivity;

/**
 * Created by mazda on 1/15/17.
 */
public class TileService extends android.service.quicksettings.TileService {

    @Override
    public void onClick() {
        super.onClick();
        try {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            startActivityAndCollapse(intent);
        } catch (Exception e) {
            Intent intent = new Intent(this, LauncherActivity.class);
            startActivity(intent);
            startActivityAndCollapse(intent);
        }
    }
}