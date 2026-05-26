package com.androidcourse.moyan.network;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * 用户相关网络请求管理器
 * 负责：登录、注册、获取用户信息、修改密码/昵称/头像
 */
public class UserNetworkManager {

    private static UserNetworkManager instance;

    private UserNetworkManager() {}

    public static UserNetworkManager getInstance() {
        if (instance == null) {
            instance = new UserNetworkManager();
        }
        return instance;
    }

    /**
     * 用户登录
     * @param phone 手机号
     * @param password 密码
     * @return 服务端响应JSON字符串
     */
    public String login(String phone, String password) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "login");

            JSONObject params = new JSONObject();
            params.put("phone", phone);
            params.put("password", password);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }


    /**
     * 用户注册
     * @param phone 手机号
     * @param password 密码
     * @param nickname 昵称
     * @return 服务端响应JSON字符串
     */
    public String register(String phone, String password, String nickname) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "register");

            JSONObject params = new JSONObject();
            params.put("phone", phone);
            params.put("password", password);
            params.put("nickname", nickname);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 服务端响应JSON字符串
     */
    public String getUserInfo(int userId) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "getUserInfo");

            JSONObject params = new JSONObject();
            params.put("userId", userId);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 服务端响应JSON字符串
     */
    public String updatePassword(int userId, String oldPassword, String newPassword) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "updatePassword");

            JSONObject params = new JSONObject();
            params.put("userId", userId);
            params.put("oldPassword", oldPassword);
            params.put("newPassword", newPassword);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 修改昵称
     * @param userId 用户ID
     * @param nickname 新昵称
     * @return 服务端响应JSON字符串
     */
    public String updateNickname(int userId, String nickname) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "updateNickname");

            JSONObject params = new JSONObject();
            params.put("userId", userId);
            params.put("nickname", nickname);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 修改头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 服务端响应JSON字符串
     */
    public String updateAvatar(int userId, String avatarUrl) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "updateAvatar");

            JSONObject params = new JSONObject();
            params.put("userId", userId);
            params.put("avatarUrl", avatarUrl);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }
}