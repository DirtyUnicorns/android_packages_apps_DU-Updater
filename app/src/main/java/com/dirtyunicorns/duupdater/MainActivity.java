/*
 * Copyright (C) 2015-2018 The Dirty Unicorns Project
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

package com.dirtyunicorns.duupdater;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dirtyunicorns.duupdater.fragments.Gapps;
import com.dirtyunicorns.duupdater.fragments.Official;
import com.dirtyunicorns.duupdater.fragments.Rc;
import com.dirtyunicorns.duupdater.fragments.Weeklies;
import com.dirtyunicorns.duupdater.navigation.BottomNavigationItemView;
import com.dirtyunicorns.duupdater.navigation.BottomNavigationMenuView;
import com.dirtyunicorns.duupdater.navigation.BottomNavigationView;
import com.dirtyunicorns.duupdater.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class MainActivity extends Activity {

    private Fragment fragment;
    private static Snackbar snackbar;
    BottomNavigationView navigation;
    private static String prop = "";

    private final static int REQUEST_READ_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        InitPermissions();

        if (getActionBar() !=null) {
            getActionBar().setTitle(R.string.app_name);
            getActionBar().setElevation(4);
        }

        View view = findViewById(R.id.viewSnack);
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getBackgroundColor(getApplicationContext()));

        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.snackbar_text_color));

        if (!Utils.isOnline(this)) {
            showSnackBar(R.string.no_internet_snackbar);
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            fragment = new Official();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragmentContainer, fragment).commit();
        }

        try {
            Process process = new ProcessBuilder("/system/bin/getprop", "ro.build.ab_update").redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                prop = line;
            }
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final BottomNavigationView bottomnavigation = findViewById(R.id.navigation);

        if (prop.contains("true")) {
            bottomnavigation.findViewById(R.id.gapps).setVisibility(View.GONE);
        }

        disableShiftMode(bottomnavigation);

        bottomnavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.official:
                                fragment = new Official();
                                bottomnavigation.getMenu().getItem(0).setChecked(true);
                                break;
                            case R.id.weeklies:
                                fragment = new Weeklies();
                                bottomnavigation.getMenu().getItem(1).setChecked(true);
                                break;
                            case R.id.rc:
                                fragment = new Rc();
                                bottomnavigation.getMenu().getItem(2).setChecked(true);
                                break;
                            case R.id.gapps:
                                fragment = new Gapps();
                                bottomnavigation.getMenu().getItem(3).setChecked(true);
                                break;
                        }
                        if (!Utils.isOnline(getApplicationContext())) {
                            showSnackBar(R.string.no_internet_snackbar);
                        } else {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                        return true;
                    }
                });
    }

    public void InitPermissions() {
        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE_PERMISSION);
        }
    }

    public static void showSnackBar(int resId) {
        hideSnackBar();
        snackbar.setText(resId).show();
    }

    public static void hideSnackBar() {
        if (snackbar.isShown()) snackbar.dismiss();
    }

    public static int getBackgroundColor(Context context) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        navigation.getSelectedItemId();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView navigationView) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);

        try {
            Field shiftMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftMode.setAccessible(true);
            shiftMode.setBoolean(menuView, false);
            shiftMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
