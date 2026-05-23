package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister, tvGuestMode;
    private ProgressBar progressBar;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = new LoginViewModel();

        if (loginViewModel.isLogin()) {
            jumpToHome();
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        initViews();
        setupListeners();
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

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        loginViewModel.login(phone, password, new LoginViewModel.LoginCallback() {
            @Override
            public void onLoginSuccess(LoginResponse response) {
                setLoading(false);
                if (response != null && response.getData() != null) {
                    Toast.makeText(LoginActivity.this, "登录成功！欢迎 " + response.getData().getNickname(), Toast.LENGTH_LONG).show();
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