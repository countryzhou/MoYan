package com.androidcourse.moyan.model;

/**
 * 趋势卡片实体类 - 纯数据模型
 * 用于首页横向滚动推荐区域
 */
public class TrendCard {
    private int postId;
    private String title;
    private String author;
    private String time;
    private int imageResId;
    private int commentCount;

    public TrendCard() {}

    public TrendCard(int postId, String title, String author, String time,
                     int imageResId, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.time = time;
        this.imageResId = imageResId;
        this.commentCount = commentCount;
    }

    // ==================== Getters and Setters ====================

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
}