package com.androidcourse.moyan.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.androidcourse.moyan.R;

import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.viewmodel.SignupViewModel;

/**
 * 注册页面
 * 使用 SignupViewModel 处理注册逻辑
 */
public class SignupActivity extends AppCompatActivity {

    private EditText etPhone, etNickname, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoLogin;
    private ProgressBar progressBar;
    private SignupViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupViewModel = new SignupViewModel();
        initViews();
        setupListeners();
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvGoLogin = findViewById(R.id.tv_go_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        tvGoLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String phone = etPhone.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        setLoading(true);

        signupViewModel.register(phone, password, confirmPassword, nickname,
                new SignupViewModel.RegisterCallback() {
                    @Override
                    public void onRegisterSuccess(LoginResponse response) {
                        setLoading(false);
                        Toast.makeText(SignupActivity.this, "注册成功！请登录",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onRegisterFailure(String error) {
                        setLoading(false);
                        Toast.makeText(SignupActivity.this, error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onValidationError(String error) {
                        setLoading(false);
                        Toast.makeText(SignupActivity.this, error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
        btnRegister.setText(isLoading ? "注册中..." : "注册");
    }
}