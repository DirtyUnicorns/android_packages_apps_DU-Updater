/*
 * Copyright (C) 2018 The Dirty Unicorns Project
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

package com.dirtyunicorns.duupdater.utils;

import android.Manifest;
import android.app.Activity;
import android.app.UiModeManager;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;

import com.dirtyunicorns.duupdater.R;

public class InterfaceHelper {

    private final static int REQUEST_WRITE_STORAGE_PERMISSION = 1;

    public static void InitPermissions(Activity context) {
        if (PermissionChecker
                .checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PERMISSION);
        }
    }

    public static void showSnackBar(int resId, Snackbar snackbar) {
        hideSnackBar(snackbar);
        snackbar.setText(resId).show();
    }

    private static void hideSnackBar(Snackbar snackbar) {
        if (snackbar.isShown()) snackbar.dismiss();
    }

    public static void setBottomNavigationId(String fragment, Activity CallingActivity) {
        BottomNavigationView bottomnavigation = CallingActivity.findViewById(R.id.navigation);
        bottomnavigation.getMenu().getItem(Integer.parseInt(fragment)).setChecked(true);
        updateBottomNavigation(CallingActivity);
    }

    public static void updateBottomNavigation(Activity CallingActivity) {
        BottomNavigationView bottomnavigation = CallingActivity.findViewById(R.id.navigation);
        bottomnavigation.findViewById(R.id.gapps).setVisibility(Utils.checkProp(
                "ro.build.ab_update", true, "true") ? View.GONE : View.VISIBLE);
        bottomnavigation.findViewById(R.id.vendors).setVisibility(Utils.checkProp(
                "ro.build.ab_update", true, "true") ? View.VISIBLE : View.GONE);
        Utils.setMargins(bottomnavigation,-180,0,-180,0);
    }

    public static void  setTheme(Activity CallingActivity) {
        UiModeManager mUiModeManager =
                CallingActivity.getApplicationContext().getSystemService(UiModeManager.class);

        int mode = mUiModeManager.getNightMode();
        switch (mode) {
            case UiModeManager.MODE_NIGHT_AUTO:
                CallingActivity.setTheme(R.style.DefaultTheme);
            case UiModeManager.MODE_NIGHT_YES:
                CallingActivity.setTheme(R.style.DarkTheme);
            case UiModeManager.MODE_NIGHT_NO:
        }
    }
}
