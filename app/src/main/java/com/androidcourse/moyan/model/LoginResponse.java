package com.androidcourse.moyan.model;

public class LoginResponse {
    private int code;      // 0成功，1失败
    private String msg;    // 提示信息
    private UserData data; // 用户数据

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public UserData getData() { return data; }

    public boolean isSuccess() { return code == 0; }

    public static class UserData {
        private int userId;
        private String nickname;
        private String phone;

        public int getUserId() { return userId; }
        public String getNickname() { return nickname; }
        public String getPhone() { return phone; }
    }
}