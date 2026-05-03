package com.androidcourse.moyan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论实体类 - 纯数据模型
 */
public class Comment {
    private int commentId;
    private int postId;
    private int userId;
    private String nickname;
    private String avatarUrl;
    private String content;
    private int likeCount;
    private int replyCount;
    private long createTime;
    private boolean isLiked;
    private boolean isAnonymous;
    private Comment replyTo;
    private List<Comment> replies;
    private boolean isExpanded;

    public Comment() {
        this.replies = new ArrayList<>();
        this.isExpanded = false;
        this.replyCount = 0;
    }

    // ==================== Getters and Setters ====================

    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

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

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public Comment getReplyTo() { return replyTo; }
    public void setReplyTo(Comment replyTo) { this.replyTo = replyTo; }

    public List<Comment> getReplies() {
        if (replies == null) replies = new ArrayList<>();
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
        this.replyCount = replies != null ? replies.size() : 0;
    }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    public String getDisplayName() {
        if (isAnonymous) return "匿名用户";
        return nickname != null ? nickname : "用户" + userId;
    }

    public boolean isProfileAccessible() {
        return !isAnonymous;
    }
}