package com.dirtyunicorns.duupdater;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by mazda on 12/25/16.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent launch = new Intent(this, com.dirtyunicorns.duupdater.MainActivity.class);
        startActivity(launch);
        finish();
    }
}
