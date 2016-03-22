/*
* Copyright (C) 2015-2016 Dirty Unicorns
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
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.dirtyunicorns.duupdater.Services.UpdateCheck;
import com.dirtyunicorns.duupdater.Utils.Dialogs;
import com.dirtyunicorns.duupdater.Utils.MainUtils;
import com.dirtyunicorns.duupdater.Utils.Vars;
import com.dirtyunicorns.duupdater.adapters.TabsPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private String[] tabTitles;
    private TabsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Context ctx;
    private ProgressDialog pd;

    private final static int REQUEST_READ_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mServiceIntent = new Intent(this, UpdateCheck.class);
        startService(mServiceIntent);

        if (Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE_PERMISSION);
        } else {
            // Do absolutely NOTHING
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        String phoneModel = Build.MODEL;

        actionBar.setTitle(getString(R.string.app_name) + " - " + phoneModel);

        Vars.SetActionBar(actionBar);
        ctx = this;

        if (MainUtils.isOnline(this)) {
        	MainUtils.CheckDNS(this);
            pd = ProgressDialog.show(this, getString(R.string.app_name), getString(R.string.please_wait_message), true);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    new ArrayList<List<Map<String, String>>>();
                    Vars.dirs = MainUtils.getDirs();
                    if (Vars.dirs != null) {
                    tabTitles = new String[Vars.dirs.length];
                    getTitles();
                    mSectionsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), Vars.dirs.length);
                    mViewPager = (ViewPager) findViewById(R.id.pager);
                    mViewPager.setOffscreenPageLimit(Vars.dirs.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setAdapter(mSectionsPagerAdapter);
                            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                                @Override
                                public void onPageSelected(int position) {
                                    actionBar.setSelectedNavigationItem(position);
                                }

                                @Override
                                public void onPageScrolled(int arg0, float arg1, int arg2) {
                                }

                                @Override
                                public void onPageScrollStateChanged(int arg0) {
                                }
                            });
                            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                                actionBar.addTab(
                                        actionBar.newTab()
                                                .setText(tabTitles[i])
                                                .setTabListener((ActionBar.TabListener) ctx));
                            }



                            pd.dismiss();
                        }
                    });
                    } else {
                    	runOnUiThread(new Runnable() {
                    		@Override
                    		public void run() {
                    			Dialogs.DeviceNotFound(ctx);
                    		}
                    	});
                    }
                }
            }).start();
        } else {
            Dialogs.OfflineDialog(this);
        }

    }

    public void getTitles() {
        for (int i = 0; i < Vars.dirs.length; i++) {
            tabTitles[i] = Vars.dirs[i];
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.hide_launcher).setChecked(!isLauncherIconEnabled());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hide_launcher:
                boolean checked = item.isChecked();
                item.setChecked(!checked);
                setLauncherIconEnabled(checked);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLauncherIconEnabled(boolean enabled) {
        int newState;
        PackageManager pm = getPackageManager();
        if (enabled) {
            newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        pm.setComponentEnabledSetting(new ComponentName(this, com.dirtyunicorns.duupdater.LauncherActivity.class), newState, PackageManager.DONT_KILL_APP);
    }

    public boolean isLauncherIconEnabled() {
        PackageManager pm = getPackageManager();
        return (pm.getComponentEnabledSetting(new ComponentName(this, com.dirtyunicorns.duupdater.LauncherActivity.class)) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

}
