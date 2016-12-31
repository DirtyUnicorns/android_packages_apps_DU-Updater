package com.dirtyunicorns.duupdater.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.dirtyunicorns.duupdater.R;

/**
 * Created by mazda on 12/30/16.
 */
public class Preferences extends Activity {


    private SharedPreferences sharedPreferences;
    public static Context context;

    public static final String NavigationTint = "NavigationTint";

    public Boolean getNavigationTint() {
        return sharedPreferences.getBoolean(NavigationTint, false);
    }

    public Preferences(Context context) {
        Preferences.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void themeMe(Activity activity) {

        Preferences preferences = new Preferences(activity);

        if (preferences.getNavigationTint()) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(context, (R.color.navigation_bar_color_tint_enabled)));
        } else {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(context, (R.color.navigation_bar_color_tint_disabled)));
        }
    }
}