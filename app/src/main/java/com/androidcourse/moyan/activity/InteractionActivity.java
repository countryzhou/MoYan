package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 互动通知页面
 */
public class InteractionActivity extends AppCompatActivity {

    private LinearLayout navHome, navExplore, navMessages, navProfile;
    private SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化 SharedPrefsHelper
        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        setContentView(R.layout.activity_interaction);

        initViews();
        setupListeners();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
    }

    /**
     * 设置点击事件监听器
     */
    private void setupListeners() {
        // 首页按钮
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(InteractionActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // 探索按钮（当前页面）
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Toast.makeText(this, "当前已在探索页面", Toast.LENGTH_SHORT).show();
            });
        }

        // 消息按钮
        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                if (sharedPrefsHelper.isGuestMode()) {
                    Toast.makeText(this, "请先登录后再查看消息", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(InteractionActivity.this, MessageActivity.class);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // 个人主页按钮
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                if (sharedPrefsHelper.isGuestMode() || !sharedPrefsHelper.isLogin()) {
                    if (!checkLogin()) return;
                }
                Intent intent = new Intent(InteractionActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    /**
     * 检查登录状态
     * @return true 如果已登录，false 否则
     */
    private boolean checkLogin() {
        if (!sharedPrefsHelper.isLogin()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(InteractionActivity.this, LoginActivity.class));
            return false;
        }
        return true;
    }

    /**
     * 兼容新旧版本的过渡动画
     */
    @SuppressWarnings("deprecation")
    private void overridePendingTransitionCompat(int enterAnim, int exitAnim) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ 使用新 API
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim);
        } else {
            // Android 13 及以下使用旧 API
            overridePendingTransition(enterAnim, exitAnim);
        }
    }
}
