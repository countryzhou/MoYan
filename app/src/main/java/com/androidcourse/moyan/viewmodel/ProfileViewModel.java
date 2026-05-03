package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.repository.UserRepository;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 个人主页ViewModel
 * 负责：加载用户信息、退出登录
 * 调用：UserRepository + SharedPrefsHelper
 */
public class ProfileViewModel {

    private UserRepository userRepository;
    private Handler mainHandler;

    public ProfileViewModel() {
        userRepository = new UserRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    /**
     * 加载用户信息（从服务器刷新）
     * @param userId 用户ID
     * @param callback 回调
     */
    public void loadUserInfo(int userId, UserInfoCallback callback) {
        userRepository.getUserInfo(userId, new UserRepository.RepositoryCallback<User>() {
            @Override
            public void onResult(User result) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(result);
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onFailure(error);
                });
            }
        });
    }

    /**
     * 退出登录
     */
    public void logout() {
        userRepository.logout();
    }

    /**
     * 是否已登录
     */
    public boolean isLogin() {
        return userRepository.isLogin();
    }

    /**
     * 用户信息回调接口
     */
    public interface UserInfoCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }
}