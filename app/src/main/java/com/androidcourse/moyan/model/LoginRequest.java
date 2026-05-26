package com.androidcourse.moyan.model;

/**
 * 登录请求实体类 - 纯数据模型
 */
public class LoginRequest {
    private String action;
    private Params params;


    public LoginRequest(String phone, String password) {
        this.action = "login";
        this.params = new Params(phone, password);
    }

    private static class Params {
        private String phone;
        private String password;

        public Params(String phone, String password) {
            this.phone = phone;
            this.password = password;
        }
    }
}