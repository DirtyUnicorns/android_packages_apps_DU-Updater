package com.dirtyunicorns.duupdater2.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by mazwoz on 10/12/16.
 */

public class CurrentVersion extends Update implements Parcelable{

    private SimpleDateFormat dateFormat;

    public void GetInfo() {
        String[] buildInfo = GetProp().split("_");
        dateFormat = new SimpleDateFormat("yyyyMMdd-hhmm");
        try {
            buildDate = dateFormat.parse(buildInfo[3].split("\\.")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        androidVersion = buildInfo[2];
        buildType = buildInfo[3].split("\\.")[2];
        majorVersion = Integer.valueOf(buildInfo[3].split("\\.")[1].replace("v",""));
        minorVersion = Integer.valueOf(buildInfo[3].split("\\.")[2].split("-")[0]);

        if (buildType.equals("OFFICIAL")) {
            isOffical = true;
            isWeekly = false;
            isTest = false;
        } else if (buildType.equals("WEEKLIES")) {
            isWeekly = true;
            isOffical = false;
            isTest = false;
        } else if (buildType.equals("TEST")) {
            isTest = true;
            isOffical = false;
            isWeekly = false;
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
