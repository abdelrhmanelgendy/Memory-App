package com.example.myapplication.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class LanguageHelper {
    public static final String APP_SETTING = "memoryAppSetting";

    public static final String ARABIC_CODE = "ar";
    public static final String ENGLISH_CODE = "en";
    private static final String CURRENT_LANGUAGE = "currentLanguage";


    public static String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static void setLocale(Activity activity, String languageCode) {
        String language = Locale.getDefault().getLanguage();
        Locale locale = new Locale(languageCode);
        if (locale.getLanguage().equals(language)) {
            return;
        }

        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
//        activity.recreate();
    }

 public static    void persist(Context context, String languageCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(CURRENT_LANGUAGE, languageCode);
        edit.apply();
    }
    public static String getPersistData(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_SETTING, Context.MODE_PRIVATE);
        String sharedPrefMode = sharedPreferences.getString(CURRENT_LANGUAGE, ENGLISH_CODE);
        return sharedPrefMode;

    }

}
