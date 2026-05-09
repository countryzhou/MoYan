package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout llFavorite, llMessageRecord, llPostRecord, llTipRecord;
    private LinearLayout llWallet, llEditProfile, llLogout, llReportRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupListeners();
        loadUserInfo();
    }

    private void initViews() {
        llFavorite = findViewById(R.id.ll_favorite);
        llMessageRecord = findViewById(R.id.ll_message_record);
        llPostRecord = findViewById(R.id.ll_post_record);
        llTipRecord = findViewById(R.id.ll_tip_record);
        llWallet = findViewById(R.id.ll_wallet);
        llEditProfile = findViewById(R.id.ll_edit_profile);
        llLogout = findViewById(R.id.ll_logout);
        llReportRecord = findViewById(R.id.ll_report_record);
    }

    private void setupListeners() {
        llFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
        llMessageRecord.setOnClickListener(v -> startActivity(new Intent(this, MessageRecordActivity.class)));
        llPostRecord.setOnClickListener(v -> startActivity(new Intent(this, PostRecordActivity.class)));
        llTipRecord.setOnClickListener(v -> startActivity(new Intent(this, TipRecordActivity.class)));
        llWallet.setOnClickListener(v -> startActivity(new Intent(this, WalletActivity.class)));
        llEditProfile.setOnClickListener(v -> startActivity(new Intent(this, EditprofileActivity.class)));
        llReportRecord.setOnClickListener(v -> startActivity(new Intent(this, ReportRecordActivity.class)));
        llLogout.setOnClickListener(v -> {
            // 退出登录，跳回登录页
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserInfo() {
        TextView tvNickname = findViewById(R.id.tv_nickname);
        TextView tvUserId = findViewById(R.id.tv_user_id);
        // TODO: 从服务端获取真实数据，先用假数据
        tvNickname.setText("测试用户");
        tvUserId.setText("ID: 10001");
    }
}