package com.androidcourse.moyan.model;

/**
 * 登录响应实体类 - 纯数据模型
 */
public class LoginResponse {
    private int code;
    private String msg;
    private UserData data;

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public UserData getData() { return data; }
    public void setData(UserData data) { this.data = data; }

    public boolean isSuccess() { return code == 0; }

    public static class UserData {
        private int userId;
        private String nickname;
        private String phone;
        private String avatarUrl;
        private int warningCount;
        private boolean isBanned;
        private String token;

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

        public int getWarningCount() { return warningCount; }
        public void setWarningCount(int warningCount) { this.warningCount = warningCount; }

        public boolean isBanned() { return isBanned; }
        public void setBanned(boolean banned) { isBanned = banned; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}