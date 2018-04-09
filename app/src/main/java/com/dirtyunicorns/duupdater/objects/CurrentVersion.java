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

package com.dirtyunicorns.duupdater.objects;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CurrentVersion extends Update implements Parcelable{

    public void GetInfo() {
        String[] buildInfo = GetProp().split("_");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmm");
        try {
            buildDate = dateFormat.parse(buildInfo[3].split("\\.")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        androidVersion = buildInfo[2];
        buildType = buildInfo[3].split("\\.")[2];
        majorVersion = Integer.parseInt(buildInfo[3].split("\\.")[1].replace("v",""));
        minorVersion = Integer.valueOf(buildInfo[3].split("\\.")[2].split("-")[0]);

        switch (buildType) {
            case "OFFICIAL":
                isOffical = true;
                isWeekly = false;
                isRc = false;
                break;
            case "WEEKLIES":
                isWeekly = true;
                isOffical = false;
                isRc = false;
                break;
            case "RC":
                isRc = true;
                isOffical = false;
                isWeekly = false;
                break;
            default:
                break;
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public CurrentVersion createFromParcel(Parcel in) {
            return new CurrentVersion();
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}