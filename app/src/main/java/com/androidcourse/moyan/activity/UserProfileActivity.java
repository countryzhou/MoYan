package com.androidcourse.moyan.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.androidcourse.moyan.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 用户主页（查看他人主页）
 * 通过userId搜索或从帖子头像点击进入
 */
public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvUserId;
    private int targetUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        targetUserId = getIntent().getIntExtra("user_id", -1);

        // TODO: 根据 targetUserId 加载用户信息
        // 调用 UserRepository.getUserInfo
        Toast.makeText(this, "查看用户ID: " + targetUserId, Toast.LENGTH_SHORT).show();
    }
}