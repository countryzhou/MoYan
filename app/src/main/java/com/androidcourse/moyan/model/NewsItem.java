package com.androidcourse.moyan.model;

/**
 * 新闻/帖子列表项 - 纯数据模型
 * 用于首页竖向新闻列表
 */
public class NewsItem {
    private int id;
    private String title;
    private String author;
    private long publishTime;
    private int imageResId;
    private int commentCount;

    public NewsItem() {}

    public NewsItem(int id, String title, String summary, String author,
                    long publishTime, int imageResId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishTime = publishTime;
        this.imageResId = imageResId;
        this.commentCount = 0;
    }

    // ==================== Getters and Setters ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public long getPublishTime() { return publishTime; }
    public void setPublishTime(long publishTime) { this.publishTime = publishTime; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
}