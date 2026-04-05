package com.moyan.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    private static SharedPreferences sp;

    public static void init(Context ctx) {
        sp = ctx.getSharedPreferences("app_data", Context.MODE_PRIVATE);
    }

    public static void putBoolean(String key, boolean val) {
        sp.edit().putBoolean(key, val).apply();
    }

    public static boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }
}
