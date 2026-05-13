package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.BuildConfig;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.LoginViewModel;

/**
 * 登录页面
 * 使用 LoginViewModel 处理登录逻辑
 * 支持 BuildConfig.IS_DEBUG 切换 Mock/真实模式
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister, tvGuestMode;
    private ProgressBar progressBar;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = new LoginViewModel();
        initViews();
        setupListeners();

        // 如果已登录，直接跳转首页
        if (loginViewModel.isLogin()) {
            jumpToHome();
            finish();
        }
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoRegister = findViewById(R.id.tv_go_register);
        tvGuestMode = findViewById(R.id.tv_guest_mode);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        if (tvGuestMode != null) {
            tvGuestMode.setOnClickListener(v -> enterGuestMode());
        }
    }

    private void performLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        setLoading(true);

        if (BuildConfig.IS_DEBUG) {
            performMockLogin(phone, password);
        } else {
            performRealLogin(phone, password);
        }
    }

    private void performMockLogin(String phone, String password) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                setLoading(false);
                if (phone.startsWith("1") && password.equals("123456")) {
                    // ✅ 修复：创建 Mock 用户并保存
                    User mockUser = new User();
                    mockUser.setUserId(10086);
                    mockUser.setPhone(phone);
                    mockUser.setNickname("测试用户_" + phone.substring(7));
                    mockUser.setToken("mock_token_" + System.currentTimeMillis());

                    // 保存用户信息
                    SharedPrefsHelper.getInstance().saveUser(mockUser);

                    // 打印调试信息
                    android.util.Log.d("LoginActivity", "========== Mock登录成功 ==========");
                    android.util.Log.d("LoginActivity", "userId: " + mockUser.getUserId());
                    android.util.Log.d("LoginActivity", "nickname: " + mockUser.getNickname());
                    android.util.Log.d("LoginActivity", "token: " + mockUser.getToken());
                    android.util.Log.d("LoginActivity", "isLogin: " + SharedPrefsHelper.getInstance().isLogin());

                    Toast.makeText(this, "登录成功！欢迎 " + mockUser.getNickname(), Toast.LENGTH_LONG).show();
                    jumpToHome();
                } else {
                    showAccountNotExistDialog();
                }
            });
        }).start();
    }

    private void performRealLogin(String phone, String password) {
        loginViewModel.login(phone, password, new LoginViewModel.LoginCallback() {
            @Override
            public void onLoginSuccess(LoginResponse response) {
                setLoading(false);

                // 验证保存结果
                android.util.Log.d("LoginActivity", "========== 登录成功回调 ==========");
                android.util.Log.d("LoginActivity", "isLogin: " + SharedPrefsHelper.getInstance().isLogin());
                android.util.Log.d("LoginActivity", "userId: " + SharedPrefsHelper.getInstance().getUserId());

                if (response != null && response.getData() != null) {
                    Toast.makeText(LoginActivity.this,
                            "登录成功！欢迎 " + response.getData().getNickname(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_LONG).show();
                }

                jumpToHome();
            }

            @Override
            public void onLoginFailure(String error) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValidationError(String error) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAccountNotExist() {
                setLoading(false);
                showAccountNotExistDialog();
            }
        });
    }

    private void showAccountNotExistDialog() {
        new AlertDialog.Builder(this)
                .setTitle("账号不存在")
                .setMessage("该账号未注册，是否前往注册？")
                .setPositiveButton("去注册", (dialog, which) -> {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("继续浏览", (dialog, which) -> {
                    enterGuestMode();
                })
                .setCancelable(false)
                .show();
    }

    private void enterGuestMode() {
        SharedPrefsHelper.getInstance().saveGuestMode();
        Toast.makeText(this, "游客模式，登录后可参与互动", Toast.LENGTH_LONG).show();
        jumpToHome();
    }

    private void jumpToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? "登录中..." : "登录");
    }
}