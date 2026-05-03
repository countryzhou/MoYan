package com.androidcourse.moyan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.androidcourse.moyan.model.User;
import com.google.gson.Gson;

/**
 * SharedPreferences 管理工具类 - 单例
 *
 * 核心功能：
 * 1. 登录状态管理（isLogin）
 * 2. 用户信息缓存（User对象）
 * 3. 身份凭证管理（Token + UserId）
 * 4. 匿名身份管理（AnonymousId + 匿名状态）
 * 5. 通用缓存/清空工具方法
 */
public class SharedPrefsHelper {

    private static final String PREF_NAME = "moyan_prefs";
    private static final String KEY_IS_LOGIN = "is_login";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_JSON = "user_json";
    private static final String KEY_IS_ANONYMOUS = "is_anonymous";
    private static final String KEY_ANONYMOUS_ID = "anonymous_id";
    private static final String KEY_ANONYMOUS_NAME = "anonymous_name";
    private static final String KEY_ANONYMOUS_COUNTER = "anonymous_counter";
    private static final String KEY_GUEST_MODE = "guest_mode";  // 新增：游客模式标志

    private static SharedPrefsHelper instance;
    private SharedPreferences prefs;
    private Gson gson;

    private SharedPrefsHelper(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPrefsHelper(context);
        }
    }

    public static SharedPrefsHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SharedPrefsHelper not initialized. Call init() first.");
        }
        return instance;
    }

    // ==================== 1. 登录状态 ====================

    public void setLogin(boolean isLogin) {
        prefs.edit().putBoolean(KEY_IS_LOGIN, isLogin).apply();
    }

    public boolean isLogin() {
        return prefs.getBoolean(KEY_IS_LOGIN, false);
    }

    // ==================== 2. 用户信息 ====================

    public void saveUser(User user) {
        if (user == null) return;
        setUserId(user.getUserId());
        setToken(user.getToken());
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_USER_JSON, userJson).apply();
        setLogin(true);
        clearGuestMode();  // 登录时清除游客模式
    }

    public User getUser() {
        String userJson = prefs.getString(KEY_USER_JSON, null);
        if (TextUtils.isEmpty(userJson)) return null;
        try {
            return gson.fromJson(userJson, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== 3. 身份凭证 ====================

    public void setUserId(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public void setToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    // ==================== 4. 匿名身份 ====================

    public void setAnonymousMode(boolean isAnonymous) {
        prefs.edit().putBoolean(KEY_IS_ANONYMOUS, isAnonymous).apply();
    }

    public boolean isAnonymousMode() {
        return prefs.getBoolean(KEY_IS_ANONYMOUS, false);
    }

    public void setAnonymousId(int anonymousId) {
        prefs.edit().putInt(KEY_ANONYMOUS_ID, anonymousId).apply();
    }

    public int getAnonymousId() {
        return prefs.getInt(KEY_ANONYMOUS_ID, -1);
    }

    public void setAnonymousName(String anonymousName) {
        prefs.edit().putString(KEY_ANONYMOUS_NAME, anonymousName).apply();
    }

    public String getAnonymousName() {
        return prefs.getString(KEY_ANONYMOUS_NAME, "匿名用户");
    }

    /**
     * 获取并递增匿名计数器
     * 用于系统自动分配匿名名（匿名1、匿名2...）
     */
    public int getAndIncrementAnonymousCounter() {
        int counter = prefs.getInt(KEY_ANONYMOUS_COUNTER, 0) + 1;
        prefs.edit().putInt(KEY_ANONYMOUS_COUNTER, counter).apply();
        return counter;
    }

    /**
     * 获取当前匿名计数器值（不递增）
     */
    public int getAnonymousCounter() {
        return prefs.getInt(KEY_ANONYMOUS_COUNTER, 0);
    }

    // ==================== 5. 游客模式（新增） ====================

    /**
     * 保存游客模式
     */
    public void saveGuestMode() {
        prefs.edit()
                .putBoolean(KEY_GUEST_MODE, true)
                .putBoolean(KEY_IS_LOGIN, false)
                .remove(KEY_USER_JSON)
                .apply();
    }

    /**
     * 清除游客模式
     */
    public void clearGuestMode() {
        prefs.edit().putBoolean(KEY_GUEST_MODE, false).apply();
    }

    /**
     * 是否为游客模式
     */
    public boolean isGuestMode() {
        return prefs.getBoolean(KEY_GUEST_MODE, false);
    }

    // ==================== 6. 通用工具方法 ====================

    /**
     * 清除所有缓存数据（退出登录时调用）
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }

    /**
     * 退出登录（清除登录相关数据，保留匿名信息）
     */
    public void logout() {
        setLogin(false);
        setUserId(-1);
        setToken("");
        clearGuestMode();
        prefs.edit().remove(KEY_USER_JSON).apply();
    }

    /**
     * 保存字符串
     */
    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    /**
     * 获取字符串
     */
    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    /**
     * 保存布尔值
     */
    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取布尔值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    /**
     * 保存整数
     */
    public void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    /**
     * 获取整数
     */
    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }
}