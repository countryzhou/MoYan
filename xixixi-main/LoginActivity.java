package com.moyan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.moyan.R;
import com.moyan.network.ApiService;
import com.moyan.network.BaseResponse;
import com.moyan.network.RetrofitClient;
import com.moyan.utils.SPUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etPhone, etCode;
    private Button btnLogin, btnGoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SPUtils.init(this);

        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        btnLogin = findViewById(R.id.btn_login);
        btnGoRegister = findViewById(R.id.btn_go_register);

        btnLogin.setOnClickListener(v -> doLogin());
        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void doLogin() {
        String phone = etPhone.getText().toString();
        String code = etCode.getText().toString();

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.login(phone, code).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    if (res.body().getCode() == 200) {
                        SPUtils.putBoolean("login", true);
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {}
        });
    }
}
