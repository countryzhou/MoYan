package com.androidcourse.moyan.activity;

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
import com.androidcourse.moyan.R;

import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 启动页
 * 判断登录状态，已登录→首页，未登录→登录页
 */
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

        btnSkip.setText(getString(R.string.skip) + " " + countdown);
        startCountdown();

        btnSkip.setOnClickListener(v -> {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            jumpToNext();
        });
    }

    private void startCountdown() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    btnSkip.setText(getString(R.string.skip) + " " + countdown);
                    countdown--;
                    handler.postDelayed(this, 1000);
                } else {
                    jumpToNext();
                }
            }
        });
    }

    /**
     * 根据登录状态跳转
     */
    private void jumpToNext() {
        Intent intent;
        if (SharedPrefsHelper.getInstance().isLogin()) {
            intent = new Intent(SplashActivity.this, HomeActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
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