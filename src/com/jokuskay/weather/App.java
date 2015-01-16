package com.jokuskay.weather;

import android.app.Application;
import android.content.SharedPreferences;

public class App extends Application {

    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = getSharedPreferences("prefs.xml", MODE_PRIVATE);
    }

    public void setPref(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setPref(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public String getPrefString(String key) {
        return preferences.getString(key, "");
    }

    public long getPrefLong(String key) {
        return preferences.getLong(key, 0);
    }

    public void clearPrefs() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
