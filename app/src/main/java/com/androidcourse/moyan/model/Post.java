package com.androidcourse.moyan.model;

import java.util.List;

/**
 * 帖子实体类
 */
public class Post {
    private int postId;
    private int userId;
    private String title;
    private String content;
    private String tags;
    private boolean isAnonymous;
    private int status;
    private int viewCount;
    private int likeCount;
    private int replyCount;
    private int collectCount;
    private int ratingCount;
    private double avgScore;
    private long createTime;
    private long updateTime;
    private String nickname;
    private String avatarUrl;
    private boolean isLiked;
    private String anonymousName;

    // 新增：图片路径列表（第一张是封面）
    private List<String> imagePaths;
    // 新增：封面图片URL（用于列表展示）
    private String coverImageUrl;

    // ==================== 原有getters/setters ====================

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public int getCollectCount() { return collectCount; }
    public void setCollectCount(int collectCount) { this.collectCount = collectCount; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public String getAnonymousName() { return anonymousName; }
    public void setAnonymousName(String anonymousName) { this.anonymousName = anonymousName; }

    public String getDisplayName() {
        if (isAnonymous && anonymousName != null) {
            return anonymousName;
        }
        return nickname != null ? nickname : "用户" + userId;
    }

    public boolean isProfileAccessible() {
        return !isAnonymous;
    }

    // ==================== 新增图片相关 ====================

    /**
     * 获取所有图片路径
     */
    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
        // 自动设置第一张为封面
        if (imagePaths != null && !imagePaths.isEmpty()) {
            this.coverImageUrl = imagePaths.get(0);
        }
    }

    /**
     * 获取封面图片URL（用于列表展示）
     */
    public String getCoverImageUrl() {
        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            return coverImageUrl;
        }
        // 兜底：取第一张
        if (imagePaths != null && !imagePaths.isEmpty()) {
            return imagePaths.get(0);
        }
        return null;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    /**
     * 判断是否有图片
     */
    public boolean hasImages() {
        return imagePaths != null && !imagePaths.isEmpty();
    }

    /**
     * 获取图片数量
     */
    public int getImageCount() {
        return imagePaths != null ? imagePaths.size() : 0;
    }
}