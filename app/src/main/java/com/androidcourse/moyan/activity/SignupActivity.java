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
import com.androidcourse.moyan.network.SocketClient;
import com.google.gson.Gson;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText etPhone, etNickname, etPassword, etConfirmPassword;  // 修改：添加确认密码
    private Button btnRegister;
    private TextView tvGoLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);           // 修改：密码输入框
        etConfirmPassword = findViewById(R.id.et_confirm_password); // 新增：确认密码输入框
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

        // 输入校验
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nickname.isEmpty()) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入11位手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "密码长度不能少于6位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        // 构建请求JSON（包含密码）
        String jsonRequest = buildRegisterJson(phone, nickname, password);

        new Thread(() -> {
            String response = SocketClient.getInstance().sendRequest(jsonRequest);
            runOnUiThread(() -> {
                setLoading(false);
                handleRegisterResponse(response);
            });
        }).start();
    }

    // 修改：添加password参数
    private String buildRegisterJson(String phone, String nickname, String password) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "register");

            JSONObject params = new JSONObject();
            params.put("phone", phone);
            params.put("nickname", nickname);
            params.put("password", password);  // 新增：密码字段
            request.put("params", params);

            return request.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"action\":\"register\",\"params\":{\"phone\":\"" + phone + "\",\"nickname\":\"" + nickname + "\",\"password\":\"" + password + "\"}}";
        }
    }

    private void handleRegisterResponse(String response) {
        try {
            Gson gson = new Gson();
            LoginResponse res = gson.fromJson(response, LoginResponse.class);

            if (res.isSuccess()) {
                Toast.makeText(this, "注册成功！请登录", Toast.LENGTH_LONG).show();
                finish(); // 返回登录页
            } else {
                String errorMsg = res.getMsg();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "注册失败，请稍后重试";
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "解析服务器响应失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
        btnRegister.setText(isLoading ? "注册中..." : "注册");
    }
}