package com.androidcourse.moyan.model;

public class RegisterRequest {
    private String action;
    private Params params;

    public RegisterRequest(String phone, String nickname) {
        this.action = "register";
        this.params = new Params(phone, nickname);
    }

    private static class Params {
        private String phone;
        private String nickname;

        public Params(String phone, String nickname) {
            this.phone = phone;
            this.nickname = nickname;
        }
    }
}