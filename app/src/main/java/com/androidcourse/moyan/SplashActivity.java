package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    private Button btnSkip;
    private Handler handler;
    private int countdown = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSkip = findViewById(R.id.btn_skip);
        handler = new Handler(Looper.getMainLooper());

        // 设置初始按钮文字："跳过 3"
        btnSkip.setText(getString(R.string.skip) + " " + countdown);

        // 开始倒计时
        startCountdown();

        // 跳过按钮点击事件
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                jumpToLogin();
            }
        });
    }

    private void startCountdown() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    // 更新按钮文字："跳过 3" -> "跳过 2" -> "跳过 1"
                    btnSkip.setText(getString(R.string.skip) + " " + countdown);
                    countdown--;
                    handler.postDelayed(this, 1000);
                } else {
                    jumpToLogin();
                }
            }
        });
    }

    private void jumpToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}