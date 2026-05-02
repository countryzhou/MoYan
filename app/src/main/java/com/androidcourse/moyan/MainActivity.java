package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.activity.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 直接跳转到首页（跳过登录）
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish(); // 关闭 MainActivity
    }
}