package com.androidcourse.moyan.model;

public class NewsItem {
    private int id;
    private String title;
    //private String summary;
    private String author;
    private long publishTime;
    private int imageResId;
    //private int likeCount;
    private int commentCount;

    // 构造函数
    public NewsItem(int id, String title, String summary, String author,
                    long publishTime, int imageResId) {
        this.id = id;
        this.title = title;
        //this.summary = summary;
        this.author = author;
        this.publishTime = publishTime;
        this.imageResId = imageResId;
        //this.likeCount = 0;
        this.commentCount = 0;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String getSummary() {
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

//    public int getLikeCount() {
//        return likeCount;
//    }
//
//    public void setLikeCount(int likeCount) {
//        this.likeCount = likeCount;
//    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}