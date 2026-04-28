package com.androidcourse.moyan.model;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.network.SocketClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrendCard {
    private int postId;
    private String title;
    private String author;
    private String time;
    private int imageResId;
    private int commentCount;

    // 回调接口
    public interface TrendCardCallback {
        void onSuccess(List<TrendCard> cards);
        void onFailure(String error);
    }

    // 完整构造函数
    public TrendCard(int postId, String title, String author, String time, int imageResId, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.time = time;
        this.imageResId = imageResId;
        this.commentCount = commentCount;
    }

    // 简化构造函数
    public TrendCard(int postId, String title, String author, int imageResId) {
        this(postId, title, author, "刚刚", imageResId, 0);
    }

    // 原有构造函数
    public TrendCard(String title, String author, int imageResId) {
        this(-1, title, author, "刚刚", imageResId, 0);
    }

    // Getters
    public int getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getTime() { return time; }
    public int getImageResId() { return imageResId; }
    public int getCommentCount() { return commentCount; }

    // Setters
    public void setPostId(int postId) { this.postId = postId; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setTime(String time) { this.time = time; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    /**
     * 从服务器获取趋势卡片数据（前4条热门帖子）
     */
    public static void fetchTrendCards(int userId, TrendCardCallback callback) {
        new Thread(() -> {
            try {
                JSONObject request = new JSONObject();
                request.put("action", "getPostList");

                JSONObject params = new JSONObject();
                params.put("page", 1);
                params.put("size", 4);
                params.put("userId", userId);
                request.put("params", params);

                // 使用 SocketClient 发送请求
                String response = SocketClient.getInstance().sendRequest(request.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("code") == 0) {
                    JSONArray posts = jsonResponse.getJSONArray("data");
                    List<TrendCard> cards = new ArrayList<>();

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        TrendCard card = new TrendCard(
                                post.getInt("postId"),
                                post.getString("title"),
                                post.getString("nickname"),
                                formatTime(post.getLong("createTime")),
                                android.R.drawable.ic_menu_gallery,
                                post.getInt("replyCount")
                        );
                        cards.add(card);
                    }

                    if (callback != null) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(cards));
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "服务器返回错误：" + jsonResponse.getInt("code"));
                    if (callback != null) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(errorMsg));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("数据解析错误：" + e.getMessage()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("请求失败：" + e.getMessage()));
                }
            }
        }).start();
    }

    /**
     * 获取默认的趋势卡片（本地数据）
     */
    public static List<TrendCard> getDefaultTrendCards() {
        List<TrendCard> cards = new ArrayList<>();
        cards.add(new TrendCard(1, "热门推荐", "官方工作室", "今日热点新闻", android.R.drawable.ic_menu_gallery, 128));
        cards.add(new TrendCard(2, "科技前沿", "科技日报", "最新科技动态", android.R.drawable.ic_menu_gallery, 89));
        cards.add(new TrendCard(3, "娱乐八卦", "娱乐周刊", "明星最新资讯", android.R.drawable.ic_menu_gallery, 256));
        cards.add(new TrendCard(4, "体育赛事", "体育频道", "精彩比赛回顾", android.R.drawable.ic_menu_gallery, 67));
        return cards;
    }

    /**
     * 格式化时间
     */
    private static String formatTime(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timestamp;

        if (diff < 60 * 1000) {
            return "刚刚";
        } else if (diff < 60 * 60 * 1000) {
            return (diff / (60 * 1000)) + "分钟前";
        } else if (diff < 24 * 60 * 60 * 1000) {
            return (diff / (60 * 60 * 1000)) + "小时前";
        } else {
            return (diff / (24 * 60 * 60 * 1000)) + "天前";
        }
    }
}