package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.repository.UserRepository;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 登录ViewModel
 * 负责：登录逻辑、表单校验
 * 调用：UserRepository
 */
public class LoginViewModel {

    private UserRepository userRepository;
    private Handler mainHandler;

    public LoginViewModel() {
        userRepository = new UserRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 登录
     * @param phone 手机号
     * @param password 密码
     * @param callback 回调
     */
    public void login(String phone, String password, LoginCallback callback) {
        // 表单校验
        String error = validateLoginForm(phone, password);
        if (error != null) {
            if (callback != null) callback.onValidationError(error);
            return;
        }

        // 调用仓库层登录
        userRepository.login(phone, password, new UserRepository.RepositoryCallback<LoginResponse>() {
            @Override
            public void onResult(LoginResponse result) {
                mainHandler.post(() -> {
                    if (result.isSuccess()) {
                        // 登录成功，清除游客模式
                        SharedPrefsHelper.getInstance().clearGuestMode();
                        if (callback != null) callback.onLoginSuccess(result);
                    } else {
                        String errorMsg = result.getMsg();
                        if (TextUtils.isEmpty(errorMsg)) {
                            errorMsg = "登录失败，请检查手机号和密码";
                        }
                        // 检查是否是账号不存在的错误
                        if (errorMsg.contains("不存在") || errorMsg.contains("未注册")) {
                            if (callback != null) callback.onAccountNotExist();
                        } else {
                            if (callback != null) callback.onLoginFailure(errorMsg);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onLoginFailure(error);
                });
            }
        });
    }

    /**
     * 进入游客模式
     */
    public void enterGuestMode() {
        SharedPrefsHelper.getInstance().saveGuestMode();
    }

    /**
     * 校验登录表单
     * @return null表示校验通过，否则返回错误信息
     */
    private String validateLoginForm(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            return "请输入手机号";
        }
        if (TextUtils.isEmpty(password)) {
            return "请输入密码";
        }
        if (phone.length() != 11) {
            return "请输入11位手机号";
        }
        if (password.length() < 6) {
            return "密码长度不能少于6位";
        }
        return null;
    }

    /**
     * 是否已登录
     */
    public boolean isLogin() {
        return userRepository.isLogin();
    }

    /**
     * 是否为游客模式
     */
    public boolean isGuestMode() {
        return SharedPrefsHelper.getInstance().isGuestMode();
    }

    /**
     * 登录回调接口
     */
    public interface LoginCallback {
        void onLoginSuccess(LoginResponse response);
        void onLoginFailure(String error);
        void onValidationError(String error);
        void onAccountNotExist();  // 新增：账号不存在回调
    }
}