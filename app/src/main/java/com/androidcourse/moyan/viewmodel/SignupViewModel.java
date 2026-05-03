package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.repository.UserRepository;

/**
 * 注册ViewModel
 * 负责：注册逻辑、表单校验
 * 调用：UserRepository
 */
public class SignupViewModel {

    private UserRepository userRepository;
    private Handler mainHandler;

    public SignupViewModel() {
        userRepository = new UserRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 注册
     * @param phone 手机号
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param nickname 昵称
     * @param callback 回调
     */
    public void register(String phone, String password, String confirmPassword,
                         String nickname, RegisterCallback callback) {
        // 表单校验
        String error = validateRegisterForm(phone, password, confirmPassword, nickname);
        if (error != null) {
            if (callback != null) callback.onValidationError(error);
            return;
        }

        // 调用仓库层注册
        userRepository.register(phone, password, nickname, new UserRepository.RepositoryCallback<LoginResponse>() {
            @Override
            public void onResult(LoginResponse result) {
                mainHandler.post(() -> {
                    if (result.isSuccess()) {
                        if (callback != null) callback.onRegisterSuccess(result);
                    } else {
                        String errorMsg = result.getMsg();
                        if (TextUtils.isEmpty(errorMsg)) {
                            errorMsg = "注册失败，请稍后重试";
                        }
                        if (callback != null) callback.onRegisterFailure(errorMsg);
                    }
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onRegisterFailure(error);
                });
            }
        });
    }

    /**
     * 校验注册表单
     */
    private String validateRegisterForm(String phone, String password,
                                        String confirmPassword, String nickname) {
        if (TextUtils.isEmpty(phone)) {
            return "请输入手机号";
        }
        if (TextUtils.isEmpty(nickname)) {
            return "请输入昵称";
        }
        if (TextUtils.isEmpty(password)) {
            return "请输入密码";
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            return "请确认密码";
        }
        if (phone.length() != 11) {
            return "请输入11位手机号";
        }
        if (password.length() < 6) {
            return "密码长度不能少于6位";
        }
        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不一致";
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            return "昵称长度需在2-20位之间";
        }
        return null;
    }

    /**
     * 注册回调接口
     */
    public interface RegisterCallback {
        void onRegisterSuccess(LoginResponse response);
        void onRegisterFailure(String error);
        void onValidationError(String error);
    }
}