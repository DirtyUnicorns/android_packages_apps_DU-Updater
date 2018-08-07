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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dirtyunicorns.duupdater.fragments.Gapps;
import com.dirtyunicorns.duupdater.fragments.Official;
import com.dirtyunicorns.duupdater.fragments.Rc;
import com.dirtyunicorns.duupdater.fragments.Vendors;
import com.dirtyunicorns.duupdater.fragments.Weeklies;
import com.dirtyunicorns.duupdater.utils.InterfaceHelper;
import com.dirtyunicorns.duupdater.utils.Utils;

import static com.dirtyunicorns.duupdater.utils.InterfaceHelper.InitPermissions;
import static com.dirtyunicorns.duupdater.utils.InterfaceHelper.setBottomNavigationId;
import static com.dirtyunicorns.duupdater.utils.InterfaceHelper.showSnackBar;

public class MainActivity extends Activity {

    BottomNavigationView navigation;
    private Fragment fragment;
    public static Snackbar snackbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        InterfaceHelper.setTheme(MainActivity.this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupInterface();
    }

    private void setupInterface() {
        InitPermissions(MainActivity.this);
        View view = findViewById(R.id.viewSnack);
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        Utils.setMargins(sbView,30,0,30,0);
        sbView.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.round_edges));

        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getColor(R.color.snackbar_text_color));
        Utils.setMargins(textView,35,0,0,0);

        BottomNavigationView bottomnavigation = findViewById(R.id.navigation);

        if (!Utils.isOnline(this)) {
            showSnackBar(R.string.no_internet_snackbar, snackbar);
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            if (Utils.checkProp("ro.du.version", false, "WEEKLIES")) {
                fragment = new Weeklies();
                setBottomNavigationId("1",MainActivity.this);
            } else if (Utils.checkProp("ro.du.version", false, "RC")) {
                fragment = new Rc();
                setBottomNavigationId("2",MainActivity.this);
            } else {
                fragment = new Official();
            }
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragmentContainer, fragment).commit();
        }

        bottomnavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.official:
                                fragment = new Official();
                                setBottomNavigationId("0",MainActivity.this);
                                break;
                            case R.id.weeklies:
                                fragment = new Weeklies();
                                setBottomNavigationId("1",MainActivity.this);
                                break;
                            case R.id.rc:
                                fragment = new Rc();
                                setBottomNavigationId("2",MainActivity.this);
                                break;
                            case R.id.gapps:
                                fragment = new Gapps();
                                setBottomNavigationId("3",MainActivity.this);
                                break;
                            case R.id.vendors:
                                fragment = new Vendors();
                                setBottomNavigationId("4",MainActivity.this);
                                break;
                            default:
                                break;
                        }
                        if (!Utils.isOnline(getApplicationContext())) {
                            showSnackBar(R.string.no_internet_snackbar,snackbar);
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

    @Override
    protected void onResume() {
        super.onResume();
        InterfaceHelper.updateBottomNavigation(MainActivity.this);
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
}