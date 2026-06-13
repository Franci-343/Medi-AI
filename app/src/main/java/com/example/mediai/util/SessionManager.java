package com.example.mediai.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(long userId, String userName, String email) {
        editor.putLong(Constants.KEY_USER_ID, userId);
        editor.putString(Constants.KEY_USER_NAME, userName);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return prefs.getLong(Constants.KEY_USER_ID, -1);
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, "");
    }

    public void saveApiKey(String apiKey) {
        editor.putString(Constants.KEY_API_KEY, apiKey);
        editor.apply();
    }

    public String getApiKey() {
        return prefs.getString(Constants.KEY_API_KEY, "");
    }

    public void saveAiProvider(String provider) {
        editor.putString(Constants.KEY_AI_PROVIDER, provider);
        editor.apply();
    }

    public String getAiProvider() {
        return prefs.getString(Constants.KEY_AI_PROVIDER, Constants.AI_PROVIDER);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}