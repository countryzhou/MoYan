package com.androidcourse.moyan.utils;

import android.content.Context;
import android.widget.Toast;

import com.androidcourse.moyan.model.User;

/**
 * 匿名辅助工具类
 * 处理匿名相关逻辑：判断匿名状态、生成匿名名称、匿名身份管理
 */
public class AnonymousHelper {

    /**
     * 判断用户当前是否为匿名身份
     */
    public static boolean isAnonymousActive(User user) {
        if (user == null) return false;
        return user.isCurrentAnonymous();
    }

    /**
     * 判断用户当前是否为匿名身份（通过SharedPrefsHelper）
     */
    public static boolean isAnonymousActive() {
        return SharedPrefsHelper.getInstance().isAnonymousMode();
    }

    /**
     * 生成匿名显示名称（匿名1、匿名2...）
     * 调用一次计数器自增一次
     */
    public static String generateAnonymousName() {
        int counter = SharedPrefsHelper.getInstance().getAndIncrementAnonymousCounter();
        return "匿名" + counter;
    }

    /**
     * 获取当前匿名显示名称
     */
    public static String getCurrentAnonymousName() {
        String name = SharedPrefsHelper.getInstance().getAnonymousName();
        if (name == null || name.isEmpty()) {
            name = generateAnonymousName();
            SharedPrefsHelper.getInstance().setAnonymousName(name);
        }
        return name;
    }

    /**
     * 切换匿名模式
     * @param enable true=开启匿名，false=关闭匿名
     */
    public static void toggleAnonymous(boolean enable) {
        SharedPrefsHelper.getInstance().setAnonymousMode(enable);
        if (enable) {
            // 开启匿名时，确保有匿名名称
            String name = SharedPrefsHelper.getInstance().getAnonymousName();
            if (name == null || name.isEmpty()) {
                name = generateAnonymousName();
                SharedPrefsHelper.getInstance().setAnonymousName(name);
            }
        }
    }

    /**
     * 判断是否允许查看用户主页
     * 匿名用户不允许被查看主页
     */
    public static boolean canViewProfile(boolean isAnonymous) {
        return !isAnonymous;
    }

    /**
     * 判断未登录用户是否只能浏览（游客模式）
     */
    public static boolean isGuestMode() {
        return SharedPrefsHelper.getInstance().isGuestMode();
    }

    /**
     * 获取当前用户ID（可能是真实ID或匿名ID）
     */
    public static int getEffectiveUserId() {
        SharedPrefsHelper sp = SharedPrefsHelper.getInstance();
        if (sp.isAnonymousMode()) {
            return sp.getAnonymousId();
        }
        // 游客模式返回 -1
        if (sp.isGuestMode()) {
            return -1;
        }
        return sp.getUserId();
    }

    /**
     * ==================== 新增：互动权限检查方法 ====================
     */

    /**
     * 检查是否可以执行需要登录的操作（点赞、评论、发帖）
     * @param context 上下文
     * @return true表示可以执行
     */
    public static boolean checkAndPromptInteract(Context context) {
        // 游客模式（未登录）不能互动
        if (isGuestMode()) {
            Toast.makeText(context, "请先登录后再进行此操作", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 检查是否已登录
        if (!SharedPrefsHelper.getInstance().isLogin()) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 检查是否可以互动（不弹提示，仅返回结果）
     */
    public static boolean canInteract() {
        return !isGuestMode() && SharedPrefsHelper.getInstance().isLogin();
    }

    /**
     * 获取当前显示名称（用于UI显示）
     */
    public static String getDisplayName() {
        SharedPrefsHelper sp = SharedPrefsHelper.getInstance();

        // 匿名模式
        if (sp.isAnonymousMode()) {
            return getCurrentAnonymousName();
        }

        // 游客模式
        if (sp.isGuestMode()) {
            return "游客";
        }

        // 登录用户
        User user = sp.getUser();
        if (user != null && user.getNickname() != null) {
            return user.getNickname();
        }
        return "用户";
    }
}