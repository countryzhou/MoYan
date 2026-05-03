package com.androidcourse.moyan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式化工具类
 */
public class TimeUtils {

    /**
     * 格式化为友好的相对时间显示
     * 例：刚刚、3分钟前、2小时前、3天前
     */
    public static String formatRelativeTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60 * 1000) {
            return "刚刚";
        } else if (diff < 60 * 60 * 1000) {
            return (diff / (60 * 1000)) + "分钟前";
        } else if (diff < 24 * 60 * 60 * 1000) {
            return (diff / (60 * 60 * 1000)) + "小时前";
        } else if (diff < 7 * 24 * 60 * 60 * 1000) {
            return (diff / (24 * 60 * 60 * 1000)) + "天前";
        } else {
            return formatDate(timestamp);
        }
    }

    /**
     * 格式化为日期字符串 MM-dd
     */
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式化为完整日期时间字符串 MM-dd HH:mm
     */
    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 格式化为完整日期时间字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String formatFullDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}