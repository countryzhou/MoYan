package com.androidcourse.moyan;

import android.app.Application;

import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * Application 类
 * 用于全局初始化
 */
public class MoYanApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 SharedPrefsHelper（必须在任何地方使用前调用）
        SharedPrefsHelper.init(this);
    }
}