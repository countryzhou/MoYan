package com.androidcourse.moyan.model;

import com.google.gson.annotations.SerializedName;

/**
 * 回复实体类
 * 完全对应服务端 ReplyDTO
 */
public class Reply {

    // ========== 服务端返回字段 ==========
    @SerializedName("replyId")
    private int replyId;

    @SerializedName("content")
    private String content;

    @SerializedName("replyTime")
    private long replyTime;

    @SerializedName("isAnonymous")
    private boolean isAnonymous;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("anonymousNum")
    private Integer anonymousNum;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    // ========== 本地扩展字段 ==========
    private int postId;
    private int userId;
    private Reply replyTo;

    // ==================== Getters and Setters ====================

    public int getReplyId() { return replyId; }
    public void setReplyId(int replyId) { this.replyId = replyId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getReplyTime() { return replyTime; }
    public void setReplyTime(long replyTime) { this.replyTime = replyTime; }

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

    // ==================== 辅助方法 ====================

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

    public long getCreateTime() { return replyTime; }
    public void setCreateTime(long createTime) { this.replyTime = createTime; }
}