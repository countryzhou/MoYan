package com.androidcourse.moyan.model;

/**
 * 注册请求实体类 - 纯数据模型
 */
public class RegisterRequest {
    private String action;
    private Params params;

    public RegisterRequest(String phone, String nickname, String password) {
        this.action = "register";
        this.params = new Params(phone, nickname, password);
    }

    private static class Params {
        private String phone;
        private String nickname;
        private String password;

        public Params(String phone, String nickname, String password) {
            this.phone = phone;
            this.nickname = nickname;
            this.password = password;
        }
    }
}