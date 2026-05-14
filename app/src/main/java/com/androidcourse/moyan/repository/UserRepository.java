package com.androidcourse.moyan.repository;

import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.network.UserNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.google.gson.Gson;

/**
 * 用户数据仓库
 * 负责用户相关的数据获取与缓存
 * 调用：UserNetworkManager + SharedPrefsHelper
 */
public class UserRepository {

    private UserNetworkManager networkManager;
    private SharedPrefsHelper spHelper;
    private Gson gson;

    public UserRepository() {
        networkManager = UserNetworkManager.getInstance();
        spHelper = SharedPrefsHelper.getInstance();
        gson = new Gson();
    }

    /**
     * 登录
     * @param phone 手机号
     * @param password 密码
     * @param callback 回调
     */
    public void login(String phone, String password, RepositoryCallback<LoginResponse> callback) {
        new Thread(() -> {
            String response = networkManager.login(phone, password);

            // 先打印原始响应
            android.util.Log.d("UserRepository", "原始响应: " + response);

            try {
                LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);

                // 检查解析后的对象
                android.util.Log.d("UserRepository", "解析成功, code: " + loginResponse.getCode());
                android.util.Log.d("UserRepository", "msg: " + loginResponse.getMsg());

                if (loginResponse.isSuccess() && loginResponse.getData() != null) {
                    android.util.Log.d("UserRepository", "用户数据: " + loginResponse.getData().getNickname());
                    User user = convertToUser(loginResponse);
                    spHelper.saveUser(user);
                    android.util.Log.d("UserRepository", "用户保存成功");
                } else {
                    android.util.Log.e("UserRepository", "登录失败或数据为空, isSuccess: " + loginResponse.isSuccess());
                }

                if (callback != null) {
                    callback.onResult(loginResponse);
                }
            } catch (Exception e) {
                android.util.Log.e("UserRepository", "解析失败", e);
                android.util.Log.e("UserRepository", "原始响应内容: " + response);
                if (callback != null) {
                    callback.onError("解析登录响应失败：" + e.getMessage() + "\n响应内容：" + response);
                }
            }
        }).start();
    }

    /**
     * 注册
     * @param phone 手机号
     * @param password 密码
     * @param nickname 昵称
     * @param callback 回调
     */
    public void register(String phone, String password, String nickname,
                         RepositoryCallback<LoginResponse> callback) {
        new Thread(() -> {
            String response = networkManager.register(phone, password, nickname);
            try {
                LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);
                if (callback != null) {
                    callback.onResult(loginResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析注册响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(int userId, RepositoryCallback<User> callback) {
        new Thread(() -> {
            String response = networkManager.getUserInfo(userId);
            try {
                LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);
                if (loginResponse.isSuccess() && loginResponse.getData() != null) {
                    User user = convertToUser(loginResponse);
                    if (callback != null) {
                        callback.onResult(user);
                    }
                } else {
                    if (callback != null) {
                        callback.onError(loginResponse.getMsg());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析用户信息失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 退出登录
     */
    public void logout() {
        spHelper.logout();
    }

    /**
     * 是否已登录
     */
    public boolean isLogin() {
        return spHelper.isLogin();
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return spHelper.getUser();
    }

    /**
     * 将 LoginResponse.UserData 转换为 User
     */
    private User convertToUser(LoginResponse loginResponse) {
        LoginResponse.UserData data = loginResponse.getData();
        User user = new User();
        user.setUserId(data.getUserId());
        user.setPhone(data.getPhone());
        user.setNickname(data.getNickname());
        user.setAvatarUrl(data.getAvatarUrl());
        user.setWarningCount(data.getWarningCount());
        user.setBanned(data.isBanned());
        user.setToken(data.getToken());
        return user;
    }

    /**
     * 通用回调接口
     */
    public interface RepositoryCallback<T> {
        void onResult(T result);
        void onError(String error);
    }
}