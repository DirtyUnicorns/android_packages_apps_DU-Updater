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

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import com.dirtyunicorns.duupdater.Services.ScheduledUpdateCheck;

/**
 * Created by mazwoz on 03.03.15.
 */
public class Settings extends PreferenceActivity {

    private SwitchPreference swUDate;
    private ComponentName mServiceComponent;
    private ListPreference updateInt;
    private Context ctx;
    private SharedPreferences sharedPrefs;

    public Settings(){}

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings_layout);

        PreferenceScreen pres = getPreferenceScreen();
        
        ctx = this;

        updateInt = (ListPreference) pres.findPreference("schedule_int");
        swUDate = (SwitchPreference) pres.findPreference("schedule_check");

        if (swUDate.isChecked()) {
        	updateInt.setEnabled(true);
        	sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        	String summary = sharedPrefs.getString("schedule_int", "1");
        	CharSequence[] values = updateInt.getEntries();
        	updateInt.setSummary(values[Integer.valueOf(summary) - 1]);
        } else {
        	updateInt.setEnabled(false);
        }
        
        updateInt.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				cancelJob();
				updateInt.setEnabled(true);
            	sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            	String updateInts = sharedPrefs.getString("schedule_int","1");

            	CharSequence[] values = updateInt.getEntries();
            	updateInt.setSummary(values[Integer.valueOf(updateInts) - 1]);
                scheduleJob(Integer.valueOf(updateInts));
				
				return true;
			}
        });
        
        swUDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {


                System.out.println("Something should happen here");
                if (swUDate.isChecked()) {
                	updateInt.setEnabled(true);
                	sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                	String updateInts = sharedPrefs.getString("schedule_int","1");

                	CharSequence[] values = updateInt.getEntries();
                	updateInt.setSummary(values[Integer.valueOf(updateInts) - 1]);
                    scheduleJob(Integer.valueOf(updateInts));
                } else {
                	updateInt.setEnabled(false);
                    cancelJob();
                }
                return false;
            }
        });

        mServiceComponent = new ComponentName(this, ScheduledUpdateCheck.class);


    }

    public void scheduleJob(int updateInt) {
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        builder.setMinimumLatency(updateInt * 86350000);
        builder.setOverrideDeadline((updateInt * 2) * 46350000);
        JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public void cancelJob() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(0);
    }
}
