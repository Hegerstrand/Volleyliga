package com.pocketpalsson.volleyball.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefsHelper {

    public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private Context context;

    public SharedPrefsHelper(Context context) {
        this.context = context;
    }


    public boolean hasUserLearnedDrawer() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    public void setUserHasLearnedDrawer() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
    }
}
