package com.androidcourse.moyan.manager;

import android.content.Context;
import android.widget.Toast;

import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.utils.AnonymousHelper;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 用户状态管理器 - 单例
 * 全局唯一的用户状态管理
 * 判断：是否登录？是否匿名？当前用户是谁？
 */
public class UserManager {

    private static UserManager instance;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * 当前是否已登录
     */
    public boolean isLogin() {
        return SharedPrefsHelper.getInstance().isLogin();
    }

    /**
     * 当前是否为匿名模式
     */
    public boolean isAnonymousMode() {
        return AnonymousHelper.isAnonymousActive();
    }

    /**
     * 当前是否为游客模式（未登录）
     */
    public boolean isGuestMode() {
        return AnonymousHelper.isGuestMode();
    }

    /**
     * 获取当前登录用户信息
     */
    public User getCurrentUser() {
        return SharedPrefsHelper.getInstance().getUser();
    }

    /**
     * 获取当前用户ID
     */
    public int getCurrentUserId() {
        return AnonymousHelper.getEffectiveUserId();
    }

    /**
     * 获取当前显示名称
     */
    public String getCurrentDisplayName() {
        return AnonymousHelper.getDisplayName();
    }

    /**
     * 是否可以执行需要登录的操作
     */
    public boolean canPerformAction() {
        return isLogin() && !isGuestMode();
    }

    /**
     * 检查并提示互动权限（带Toast）
     * @param context 上下文
     * @return true表示可以执行
     */
    public boolean checkAndPromptInteract(Context context) {
        if (isGuestMode()) {
            Toast.makeText(context, "请先登录后再进行此操作", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isLogin()) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 退出登录
     */
    public void logout() {
        SharedPrefsHelper.getInstance().logout();
    }

    /**
     * 完全清除所有数据
     */
    public void clearAll() {
        SharedPrefsHelper.getInstance().clearAll();
    }
}