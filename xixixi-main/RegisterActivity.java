package com.moyan.ui;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etPhone, etName;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etPhone = findViewById(R.id.et_phone);
        etName = findViewById(R.id.et_nickname);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String phone = etPhone.getText().toString();
        String name = etName.getText().toString();

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.register(phone, name).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    if (res.body().getCode() == 200) {
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {}
        });
    }
}
