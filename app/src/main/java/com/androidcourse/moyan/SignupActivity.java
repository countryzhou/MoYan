package com.androidcourse.moyan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// TODO: 等服务端搭好后，取消下面这些 import 的注释
import com.androidcourse.moyan.model.LoginResponse;
import com.androidcourse.moyan.network.SocketClient;
import com.google.gson.Gson;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText etPhone, etNickname, etCode;
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
        etCode = findViewById(R.id.et_code);
        btnRegister = findViewById(R.id.btn_register);
        tvGoLogin = findViewById(R.id.tv_go_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        tvGoLogin.setOnClickListener(v -> finish()); // 返回登录页
    }

    private void performRegister() {
        String phone = etPhone.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        // 输入校验
        if (phone.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nickname.isEmpty()) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (code.isEmpty()) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入11位手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);





        // 构建请求JSON
        String jsonRequest = buildRegisterJson(phone, nickname);

        new Thread(() -> {
            String response = SocketClient.getInstance().sendRequest(jsonRequest);
            runOnUiThread(() -> {
                setLoading(false);
                handleRegisterResponse(response);
            });
        }).start();

    }



    private String buildRegisterJson(String phone, String nickname) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "register");

            JSONObject params = new JSONObject();
            params.put("phone", phone);
            params.put("nickname", nickname);
            request.put("params", params);

            return request.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"action\":\"register\",\"params\":{\"phone\":\"" + phone + "\",\"nickname\":\"" + nickname + "\"}}";
        }
    }

    private void handleRegisterResponse(String response) {
        try {
            Gson gson = new Gson();
            // 注册响应格式和登录一样
            LoginResponse res = gson.fromJson(response, LoginResponse.class);

            if (res.isSuccess()) {
                Toast.makeText(this, "注册成功！", Toast.LENGTH_LONG).show();
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