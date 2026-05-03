package com.androidcourse.moyan.model;

/**
 * 回复实体类 - 纯数据模型
 * 对应API：getReplies、createReply
 */
public class Reply {
    private int replyId;
    private int postId;
    private int userId;
    private String nickname;
    private String avatarUrl;
    private String content;
    private int likeCount;
    private long createTime;
    private boolean isLiked;
    private boolean isAnonymous;
    private Reply replyTo;
    private int replyToId;

    // ==================== Getters and Setters ====================

    public int getReplyId() { return replyId; }
    public void setReplyId(int replyId) { this.replyId = replyId; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public Reply getReplyTo() { return replyTo; }
    public void setReplyTo(Reply replyTo) { this.replyTo = replyTo; }

    public int getReplyToId() { return replyToId; }
    public void setReplyToId(int replyToId) { this.replyToId = replyToId; }

    public String getDisplayName() {
        if (isAnonymous) return "匿名用户";
        return nickname != null ? nickname : "用户" + userId;
    }

    public boolean isProfileAccessible() {
        return !isAnonymous;
    }
}