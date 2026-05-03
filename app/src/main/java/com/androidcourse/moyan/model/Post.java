package com.androidcourse.moyan.model;

/**
 * 帖子实体类 - 纯数据模型
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
    private int ratingCount;
    private double avgScore;
    private long createTime;
    private long updateTime;
    private String nickname;
    private String avatarUrl;
    private boolean isLiked;
    private String anonymousName;

    // ==================== Getters and Setters ====================

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

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        if (isAnonymous && anonymousName != null) {
            return anonymousName;
        }
        return nickname != null ? nickname : "用户" + userId;
    }

    /**
     * 是否允许点击头像进入主页
     */
    public boolean isProfileAccessible() {
        return !isAnonymous;
    }
}