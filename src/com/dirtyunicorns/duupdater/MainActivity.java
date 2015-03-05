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

package com.dirtyunicorns.duupdater;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mServiceIntent = new Intent(this, UpdateCheck.class);
        startService(mServiceIntent);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        String phoneModel = Build.MODEL;

        actionBar.setTitle("DU Updater - " + phoneModel);

        Vars.SetActionBar(actionBar);
        ctx = this;

        if (MainUtils.isOnline(this)) {
        	MainUtils.CheckDNS(this);
            pd = ProgressDialog.show(this, "DU Updater", "Please wait while search for updates...", true);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent settings = new Intent(this, Settings.class);
            startActivity(settings);

            return true;
        }

        return super.onOptionsItemSelected(item);
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
