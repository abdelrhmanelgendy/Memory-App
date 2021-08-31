package com.example.myapplication.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class DarkModeHelper {
    public static final String NIGHT_MODE = "nightMode";
    public static final String LIGHT_MODE = "lightMode";
    public static final String CURRENT_MODE = "currentMode";
    private static Context mContext;
    public static final String APP_SETTING = "memoryAppSetting";

    public static void init(Context c) {
        mContext = c;
    }

    public static String getCurrentMode() {
        int defaultNightMode = AppCompatDelegate.getDefaultNightMode();
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return NIGHT_MODE;
        } else {
            return LIGHT_MODE;
        }

    }

    public static void setMode(int modeCode) {
        String currentMode = (modeCode == AppCompatDelegate.MODE_NIGHT_YES) ? NIGHT_MODE : LIGHT_MODE;
        persist(currentMode);
    }

    private static void persist(String mode) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(CURRENT_MODE, mode);
        edit.apply();
    }
    public static String getPersistData()
    {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        String sharedPrefMode = sharedPreferences.getString(CURRENT_MODE, LIGHT_MODE);
        return sharedPrefMode;

    }
    public static boolean iSNight()
    {
        if (DarkModeHelper.getCurrentMode().equals(DarkModeHelper.NIGHT_MODE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


}
