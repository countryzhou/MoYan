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
    private TextView tvGoRegister, tvGuestMode;  // 添加游客模式TextView
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
        tvGuestMode = findViewById(R.id.tv_guest_mode);  // 需要在布局中添加
        progressBar = findViewById(R.id.progress_bar);

        // 如果布局中没有tv_guest_mode，可以注释掉上面这行，使用下面的方式创建提示
        // 或者添加一个游客模式的TextView到activity_login.xml
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // 游客模式点击事件
        if (tvGuestMode != null) {
            tvGuestMode.setOnClickListener(v -> enterGuestMode());
        } else {
            // 如果没有tv_guest_mode，可以添加一个提示
            findViewById(android.R.id.content).post(() -> {
                Toast.makeText(this, "点击返回键可进入游客模式", Toast.LENGTH_LONG).show();
            });
        }
    }

    private void performLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        setLoading(true);

        if (BuildConfig.IS_DEBUG) {
            // Debug: Mock模式
            performMockLogin(phone, password);
        } else {
            // Release: 真实网络请求
            performRealLogin(phone, password);
        }
    }

    /**
     * Mock模式登录
     */
    private void performMockLogin(String phone, String password) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                setLoading(false);
                // Mock模式：只有以1开头且密码123456才成功，否则显示账号不存在
                if (phone.startsWith("1") && password.equals("123456")) {
                    Toast.makeText(this, "登录成功！", Toast.LENGTH_LONG).show();
                    jumpToHome();
                } else {
                    // 显示账号不存在对话框
                    showAccountNotExistDialog();
                }
            });
        }).start();
    }

    /**
     * 真实模式登录（使用ViewModel）
     */
    private void performRealLogin(String phone, String password) {
        loginViewModel.login(phone, password, new LoginViewModel.LoginCallback() {
            @Override
            public void onLoginSuccess(LoginResponse response) {
                setLoading(false);
                Toast.makeText(LoginActivity.this,
                        "登录成功！欢迎 " + response.getData().getNickname(),
                        Toast.LENGTH_LONG).show();
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

    /**
     * 显示账号不存在对话框
     */
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

    /**
     * 进入游客模式
     */
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