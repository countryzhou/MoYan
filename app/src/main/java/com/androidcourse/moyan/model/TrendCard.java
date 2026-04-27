package com.androidcourse.moyan.model;

public class TrendCard {
    private int postId;          // 新增：帖子ID
    private String title;        // 标题
    private String author;       // 作者/工作室名称
    private String time;         // 发布时间
    private int imageResId;      // 图片资源ID
    private int commentCount;    // 评论数量

    // 完整构造函数
    public TrendCard(int postId, String title, String author, String time, int imageResId, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.time = time;
        this.imageResId = imageResId;
        this.commentCount = commentCount;
    }

    // 简化构造函数（不带时间和评论数）
    public TrendCard(int postId, String title, String author, int imageResId) {
        this.postId = postId;
        this.title = title;
        this.author = author;
        this.imageResId = imageResId;
        this.time = "12:00";
        this.commentCount = 0;
    }

    // 原有构造函数保留兼容性
    public TrendCard(String title, String author, int imageResId) {
        this.postId = -1;
        this.title = title;
        this.author = author;
        this.imageResId = imageResId;
        this.time = "12:00";
        this.commentCount = 0;
    }

    // Getter 和 Setter
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}