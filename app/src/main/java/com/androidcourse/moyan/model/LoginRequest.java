package com.androidcourse.moyan.model;

/**
 * 登录请求实体类 - 纯数据模型
 */
public class LoginRequest {
    private String action;
    private Params params;

    public LoginRequest(String phone, String code) {
        this.action = "login";
        this.params = new Params(phone, code);
    }

    private static class Params {
        private String phone;
        private String code;

        public Params(String phone, String code) {
            this.phone = phone;
            this.code = code;
        }
    }
}