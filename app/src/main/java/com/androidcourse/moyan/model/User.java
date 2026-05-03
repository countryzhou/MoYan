package com.androidcourse.moyan.model;

/**
 * 用户实体类 - 纯数据模型
 * 包含用户基本信息、匿名状态、警告封禁状态
 */
public class User {
    private int userId;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private boolean isAnonymous;
    private int warningCount;
    private boolean isBanned;
    private String token;
    private String anonymousName;
    private boolean currentAnonymous;

    public User() {}

    public User(int userId, String phone, String nickname, String avatarUrl) {
        this.userId = userId;
        this.phone = phone;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.isAnonymous = false;
        this.warningCount = 0;
        this.isBanned = false;
    }

    // ==================== Getters and Setters ====================

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public int getWarningCount() { return warningCount; }
    public void setWarningCount(int warningCount) { this.warningCount = warningCount; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getAnonymousName() { return anonymousName; }
    public void setAnonymousName(String anonymousName) { this.anonymousName = anonymousName; }

    public boolean isCurrentAnonymous() { return currentAnonymous; }
    public void setCurrentAnonymous(boolean currentAnonymous) { this.currentAnonymous = currentAnonymous; }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        if (currentAnonymous && anonymousName != null) {
            return anonymousName;
        }
        return nickname != null ? nickname : "用户" + userId;
    }

    /**
     * 是否允许点击头像进入主页
     */
    public boolean isProfileAccessible() {
        return !currentAnonymous && !isBanned;
    }
}