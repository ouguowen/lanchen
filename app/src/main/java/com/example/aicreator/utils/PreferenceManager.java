package com.example.aicreator.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 偏好设置管理器
 * 用于管理用户的登录状态和首选项
 */
public class PreferenceManager {

    private static final String PREF_NAME = "ai_creator_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 设置登录状态
     * @param isLoggedIn 是否已登录
     */
    public void setLoggedIn(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    /**
     * 获取用户ID
     * @return 用户ID，如果未设置则返回-1
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    /**
     * 获取用户名
     * @return 用户名，如果未设置则返回null
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    /**
     * 设置主题
     * @param theme 主题（"light" 或 "dark"）
     */
    public void setTheme(String theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_THEME, theme);
        editor.apply();
    }

    /**
     * 获取主题
     * @return 主题，默认为"light"
     */
    public String getTheme() {
        return sharedPreferences.getString(KEY_THEME, "light");
    }

    /**
     * 设置语言
     * @param language 语言代码（如"zh-CN"）
     */
    public void setLanguage(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    /**
     * 获取语言
     * @return 语言代码，默认为"zh-CN"
     */
    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "zh-CN");
    }

    /**
     * 设置通知状态
     * @param enabled 是否启用通知
     */
    public void setNotificationsEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIFICATIONS, enabled);
        editor.apply();
    }

    /**
     * 获取通知状态
     * @return 是否启用通知，默认为true
     */
    public boolean areNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true);
    }

    /**
     * 清除所有偏好设置（用于注销）
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
} 