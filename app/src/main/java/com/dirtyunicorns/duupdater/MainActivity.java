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
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.UiModeManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.PermissionChecker;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dirtyunicorns.duupdater.fragments.Gapps;
import com.dirtyunicorns.duupdater.fragments.Official;
import com.dirtyunicorns.duupdater.fragments.Rc;
import com.dirtyunicorns.duupdater.fragments.Vendors;
import com.dirtyunicorns.duupdater.fragments.Weeklies;
import com.dirtyunicorns.duupdater.utils.Utils;

public class MainActivity extends Activity {

    BottomNavigationView navigation;
    Fragment fragment;
    static Snackbar snackbar;
    UiModeManager mUiModeManager;

    private final static int REQUEST_WRITE_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        InitPermissions();

        View view = findViewById(R.id.viewSnack);
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        Utils.setMargins(sbView,30,0,30,0);
        sbView.setBackground(getResources().getDrawable(R.drawable.round_edges));

        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getColor(R.color.snackbar_text_color));
        Utils.setMargins(textView,35,0,0,0);

        final BottomNavigationView bottomnavigation = findViewById(R.id.navigation);

        if (!Utils.isOnline(this)) {
            showSnackBar(R.string.no_internet_snackbar);
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            if (Utils.checkProp("ro.du.version", false, "WEEKLIES")) {
                fragment = new Weeklies();
                setBottomNavigationId("1");
            } else if (Utils.checkProp("ro.du.version", false, "RC")) {
                fragment = new Rc();
                setBottomNavigationId("2");
            } else {
                fragment = new Official();
            }
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragmentContainer, fragment).commit();
        }

        bottomnavigation.findViewById(R.id.gapps).setVisibility(Utils.checkProp(
                "ro.build.ab_update", true, "true") ? View.GONE : View.VISIBLE);
        bottomnavigation.findViewById(R.id.vendors).setVisibility(Utils.checkProp(
                "ro.build.ab_update", true, "true") ? View.VISIBLE : View.GONE);
        Utils.setMargins(bottomnavigation,-180,0,-180,0);

        bottomnavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.official:
                                fragment = new Official();
                                setBottomNavigationId("0");
                                break;
                            case R.id.weeklies:
                                fragment = new Weeklies();
                                setBottomNavigationId("1");
                                break;
                            case R.id.rc:
                                fragment = new Rc();
                                setBottomNavigationId("2");
                                break;
                            case R.id.gapps:
                                fragment = new Gapps();
                                setBottomNavigationId("3");
                                break;
                            case R.id.vendors:
                                fragment = new Vendors();
                                setBottomNavigationId("4");
                                break;
                            default:
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

                        bottomnavigation.findViewById(R.id.gapps).setVisibility(Utils.checkProp(
                                "ro.build.ab_update", true, "true") ? View.GONE : View.VISIBLE);
                        bottomnavigation.findViewById(R.id.vendors).setVisibility(Utils.checkProp(
                                "ro.build.ab_update", true, "true") ? View.VISIBLE : View.GONE);
                        Utils.setMargins(bottomnavigation,-180,0,-180,0);
                        return true;
                    }
                });
    }

    public void InitPermissions() {
        if (PermissionChecker
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_PERMISSION);
        }
    }

    public static void showSnackBar(int resId) {
        hideSnackBar();
        snackbar.setText(resId).show();
    }

    public static void hideSnackBar() {
        if (snackbar.isShown()) snackbar.dismiss();
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

    private void setBottomNavigationId(String fragment) {
        BottomNavigationView bottomnavigation = findViewById(R.id.navigation);
        bottomnavigation.getMenu().getItem(Integer.parseInt(fragment)).setChecked(true);
    }

    private void setTheme() {
        mUiModeManager = getApplicationContext().getSystemService(UiModeManager.class);

        int mode = mUiModeManager.getNightMode();
        switch (mode) {
            case UiModeManager.MODE_NIGHT_AUTO:
                setTheme(R.style.DefaultTheme);
            case UiModeManager.MODE_NIGHT_YES:
                setTheme(R.style.DarkTheme);
            case UiModeManager.MODE_NIGHT_NO:
        }
    }
}