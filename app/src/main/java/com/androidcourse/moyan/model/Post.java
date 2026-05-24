package com.androidcourse.moyan.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 帖子实体类
 * 完全对应服务端 PostListDTO + PostDetailDTO
 */
public class Post {

    // ========== 服务端返回字段 ==========
    @SerializedName("postId")
    private int postId;

    @SerializedName("title")
    private String title;

    @SerializedName("contentPreview")
    private String contentPreview;

    @SerializedName("content")
    private String content;

    @SerializedName("tags")
    private String tags;

    @SerializedName("postTime")
    private String postTimeString;

    private transient long postTime;

    @SerializedName("viewCount")
    private int viewCount;

    @SerializedName("isNewbie")
    private boolean isNewbie;

    @SerializedName("totalScore")
    private double totalScore;

    @SerializedName("replyCount")
    private int replyCount;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("isAnonymous")
    private boolean isAnonymous;

    // 帖子详情专用
    @SerializedName("canUserRate")
    private boolean canUserRate;

    @SerializedName("userRatingTag")
    private int userRatingTag;

    @SerializedName("userRatingArticle")
    private int userRatingArticle;

    @SerializedName("replies")
    private List<Reply> replies;

    // 用户交互字段
    @SerializedName("avatarUrl")
    private String avatarUrl;

    // ========== 本地扩展字段 ==========
    private int userId;
    private long updateTime;
    private int collectCount;
    private boolean isFollowed;
    private List<String> imagePaths;
    private String coverImageUrl;

    // ==================== Getters and Setters ====================

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContentPreview() { return contentPreview; }
    public void setContentPreview(String contentPreview) { this.contentPreview = contentPreview; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public boolean isNewbie() { return isNewbie; }
    public void setNewbie(boolean newbie) { isNewbie = newbie; }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public boolean isCanUserRate() { return canUserRate; }
    public void setCanUserRate(boolean canUserRate) { this.canUserRate = canUserRate; }

    public int getUserRatingTag() { return userRatingTag; }
    public void setUserRatingTag(int userRatingTag) { this.userRatingTag = userRatingTag; }

    public int getUserRatingArticle() { return userRatingArticle; }
    public void setUserRatingArticle(int userRatingArticle) { this.userRatingArticle = userRatingArticle; }

    public List<Reply> getReplies() { return replies; }
    public void setReplies(List<Reply> replies) { this.replies = replies; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }

    public int getCollectCount() { return collectCount; }
    public void setCollectCount(int collectCount) { this.collectCount = collectCount; }

    public boolean isFollowed() { return isFollowed; }
    public void setFollowed(boolean followed) { isFollowed = followed; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
        if (imagePaths != null && !imagePaths.isEmpty()) {
            this.coverImageUrl = imagePaths.get(0);
        }
    }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    // ==================== 辅助方法 ====================

    public String getDisplayName() {
        if (isAnonymous) return "匿名用户";
        if (authorName != null && !authorName.isEmpty()) return authorName;
        return "用户" + userId;
    }

    public boolean isProfileAccessible() {
        return !isAnonymous;
    }

    public boolean hasImages() {
        return imagePaths != null && !imagePaths.isEmpty();
    }

    public int getImageCount() {
        return imagePaths != null ? imagePaths.size() : 0;
    }

    public long getCreateTime() { return postTime; }
    public void setCreateTime(long createTime) { this.postTime = createTime; }

    public long getPostTime() {
        if (postTime == 0 && postTimeString != null) {
            postTime = parseTimeString(postTimeString);
        }
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public String getPostTimeString() {
        return postTimeString;
    }

    public void setPostTimeString(String postTimeString) {
        this.postTimeString = postTimeString;
        this.postTime = 0;
    }

    private long parseTimeString(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return System.currentTimeMillis();
        }

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    "MMM dd, yyyy, h:mm:ss a", java.util.Locale.ENGLISH);
            java.util.Date date = sdf.parse(timeString);
            return date != null ? date.getTime() : System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

}