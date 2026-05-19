package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.androidcourse.moyan.R;

import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.ProfileViewModel;

/**
 * 个人主页
 * 使用 ProfileViewModel
 */
public class ProfileActivity extends AppCompatActivity {

    private LinearLayout navHome, navExplore, navMessages, navProfile;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        // 检查登录状态
        if (!SharedPrefsHelper.getInstance().isLogin()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        
        profileViewModel = new ProfileViewModel();
        initViews();
        setupBottomNavigation();
        loadUserProfile();
    }

    private void initViews() {
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupBottomNavigation() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, InteractionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        
        // 高亮当前选中的导航项
        if (navProfile != null) {
            navProfile.setSelected(true);
            // 可选：设置高亮颜色
            // navProfile.setBackgroundColor(getColor(R.color.primary_blue));
        }
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
    
    /**
     * 加载用户个人信息
     */
    private void loadUserProfile() {
        // TODO: 使用 profileViewModel 加载用户信息
        // 例如：
        // profileViewModel.loadUserInfo(new ProfileViewModel.UserCallback() {
        //     @Override
        //     public void onSuccess(User user) {
        //         // 更新 UI
        //     }
        //     
        //     @Override
        //     public void onFailure(String error) {
        //         Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
        //     }
        // });
    }
}