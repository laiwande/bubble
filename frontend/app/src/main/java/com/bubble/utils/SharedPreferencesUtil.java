package com.bubble.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static final String PREF_NAME = "bubble_pref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public void clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply();
    }

    public void saveUserId(Long userId) {
        sharedPreferences.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public Long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1L);
    }

    public void clearUserId() {
        sharedPreferences.edit().remove(KEY_USER_ID).apply();
    }

    public boolean isLoggedIn() {
        return !getToken().isEmpty();
    }
}
