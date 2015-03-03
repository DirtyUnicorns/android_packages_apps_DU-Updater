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
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import com.dirtyunicorns.duupdater.Services.ScheduledUpdateCheck;

/**
 * Created by mazwoz on 03.03.15.
 */
public class Settings extends PreferenceActivity {

    private SwitchPreference swUDate;
    private ComponentName mServiceComponent;

    public Settings(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_layout);

        PreferenceScreen pres = getPreferenceScreen();

        swUDate = (SwitchPreference) pres.findPreference("schedule_check");

        swUDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {


                System.out.println("Something should happen here");
                if (swUDate.isChecked()) {
                    scheduleJob();
                } else {
                    cancelJob();
                }
                return false;
            }
        });

        mServiceComponent = new ComponentName(this, ScheduledUpdateCheck.class);


    }

    public void scheduleJob() {
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent);
        builder.setMinimumLatency(432000000);
        builder.setOverrideDeadline(777600000);
        JobScheduler jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public void cancelJob() {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancel(0);
    }
}
