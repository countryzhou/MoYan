package com.androidcourse.moyan.model;

import com.google.gson.annotations.SerializedName;

public class Reply {

    @SerializedName("replyId")
    private int replyId;

    @SerializedName("content")
    private String content;

    @SerializedName("replyTime")
    private String replyTimeString;

    private transient long replyTime;

    @SerializedName("isAnonymous")
    private boolean isAnonymous;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("anonymousNum")
    private Integer anonymousNum;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    private int postId;
    private int userId;
    private Reply replyTo;

    public int getReplyId() { return replyId; }
    public void setReplyId(int replyId) { this.replyId = replyId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getReplyTime() {
        if (replyTime == 0 && replyTimeString != null) {
            replyTime = parseTimeString(replyTimeString);
        }
        return replyTime;
    }

    public void setReplyTime(long replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyTimeString() {
        return replyTimeString;
    }

    public void setReplyTimeString(String replyTimeString) {
        this.replyTimeString = replyTimeString;
        this.replyTime = 0;
    }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public Integer getAnonymousNum() { return anonymousNum; }
    public void setAnonymousNum(Integer anonymousNum) { this.anonymousNum = anonymousNum; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Reply getReplyTo() { return replyTo; }
    public void setReplyTo(Reply replyTo) { this.replyTo = replyTo; }

    public String getDisplayName() {
        if (isAnonymous) {
            if (anonymousNum != null) {
                return "匿名用户 #" + anonymousNum;
            }
            return "匿名用户";
        }
        if (authorName != null && !authorName.isEmpty()) return authorName;
        return "用户" + userId;
    }

    public boolean isProfileAccessible() {
        return !isAnonymous;
    }

    public long getCreateTime() { return getReplyTime(); }
    public void setCreateTime(long createTime) { this.replyTime = createTime; }

    private long parseTimeString(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return System.currentTimeMillis();
        }

        try {
            if (timeString.contains("T")) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.ENGLISH);
                java.util.Date date = sdf.parse(timeString);
                return date != null ? date.getTime() : System.currentTimeMillis();
            } else {
                String cleanedTime = timeString.replace("\u202F", " ").replace("\u00A0", " ");
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                        "MMM dd, yyyy, h:mm:ss a", java.util.Locale.ENGLISH);
                java.util.Date date = sdf.parse(cleanedTime);
                return date != null ? date.getTime() : System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }
}
