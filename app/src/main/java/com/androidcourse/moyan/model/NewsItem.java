package com.androidcourse.moyan.model;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.network.SocketClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsItem {
    private int id;
    private String title;
    private String author;
    private long publishTime;
    private int imageResId;
    private int commentCount;

    // 回调接口
    public interface NewsListCallback {
        void onSuccess(List<NewsItem> newsList);
        void onFailure(String error);
    }

    // 构造函数
    public NewsItem(int id, String title, String summary, String author,
                    long publishTime, int imageResId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishTime = publishTime;
        this.imageResId = imageResId;
        this.commentCount = 0;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public long getPublishTime() { return publishTime; }
    public int getImageResId() { return imageResId; }
    public int getCommentCount() { return commentCount; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublishTime(long publishTime) { this.publishTime = publishTime; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    /**
     * 从服务器获取新闻列表
     */
    public static void fetchNewsList(int userId, int page, int size, NewsListCallback callback) {
        new Thread(() -> {
            try {
                JSONObject request = new JSONObject();
                request.put("action", "getPostList");

                JSONObject params = new JSONObject();
                params.put("page", page);
                params.put("size", size);
                params.put("userId", userId);
                request.put("params", params);

                // 使用 SocketClient 发送请求
                String response = SocketClient.getInstance().sendRequest(request.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("code") == 0) {
                    JSONArray posts = jsonResponse.getJSONArray("data");
                    List<NewsItem> newsList = new ArrayList<>();

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        NewsItem item = new NewsItem(
                                post.getInt("postId"),
                                post.getString("title"),
                                post.getString("content"),
                                post.getString("nickname"),
                                post.getLong("createTime"),
                                android.R.drawable.ic_menu_gallery
                        );
                        item.setCommentCount(post.getInt("replyCount"));
                        newsList.add(item);
                    }

                    if (callback != null) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(newsList));
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
     * 获取默认的新闻列表（本地数据）
     */
    public static List<NewsItem> getDefaultNewsList() {
        List<NewsItem> newsList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        for (int i = 1; i <= 5; i++) {
            NewsItem item = new NewsItem(
                    i,
                    "示例新闻标题 " + i,
                    "这是示例新闻内容，当服务器没有数据时会显示这些内容。",
                    "系统作者",
                    currentTime - i * 3600000L,
                    android.R.drawable.ic_menu_gallery
            );
            item.setCommentCount((int)(Math.random() * 100));
            newsList.add(item);
        }
        return newsList;
    }

    /**
     * 获取格式化的发布时间
     */
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(publishTime));
    }
}