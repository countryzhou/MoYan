package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.network.SocketClient;
import com.google.gson.Gson;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etPassword;  // 修改：etCode -> etPassword
    private Button btnLogin;
    private TextView tvGoRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);  // 修改：使用密码输入框
        btnLogin = findViewById(R.id.btn_login);
        tvGoRegister = findViewById(R.id.tv_go_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();  // 修改：获取密码

        // 输入校验
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
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

        // 显示加载状态
        setLoading(true);

        // 构建请求JSON
        String jsonRequest = buildLoginJson(phone, password);

        // 在子线程中发送请求
        new Thread(() -> {
            String response = SocketClient.getInstance().sendRequest(jsonRequest);

            // 回到主线程处理结果
            runOnUiThread(() -> {
                setLoading(false);
                handleLoginResponse(response);
            });
        }).start();
    }

    // 修改：使用password代替code
    private String buildLoginJson(String phone, String password) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "login");

            JSONObject params = new JSONObject();
            params.put("phone", phone);
            params.put("password", password);  // 修改：code -> password
            request.put("params", params);

            return request.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"action\":\"login\",\"params\":{\"phone\":\"" + phone + "\",\"password\":\"" + password + "\"}}";
        }
    }

    private void handleLoginResponse(String response) {
        try {
            Gson gson = new Gson();
            LoginResponse loginRes = gson.fromJson(response, LoginResponse.class);

            if (loginRes.isSuccess()) {
                // 登录成功
                Toast.makeText(this, "登录成功！欢迎 " + loginRes.getData().getNickname(), Toast.LENGTH_LONG).show();

                // 保存用户信息到 SharedPreferences
                //saveUserInfo(loginRes.getData());

                // 跳转到主页
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } else {
                // 登录失败
                String errorMsg = loginRes.getMsg();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "登录失败，请检查手机号和密码";
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "解析服务器响应失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 新增：保存用户信息
//    private void saveUserInfo(LoginResponse.UserData userData) {
//        android.content.SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
//        android.content.SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("userId", userData.getUserId());
//        editor.putString("phone", userData.getPhone());
//        editor.putString("nickname", userData.getNickname());
//        editor.putString("avatarUrl", userData.getAvatarUrl());
//        editor.apply();
//    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? "登录中..." : "登录");
    }
}