package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.androidcourse.moyan.R;

import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.ProfileViewModel;
import com.bumptech.glide.Glide;

/**
 * 个人主页
 * 使用 ProfileViewModel
 */
public class ProfileActivity extends AppCompatActivity {

    private LinearLayout navHome, navExplore, navMessages, navProfile;
    private LinearLayout llFavorite, llMessageRecord, llPostRecord, llTipRecord;
    private LinearLayout llWallet, llEditProfile, llLogout, llReportRecord;
    private ImageView ivAvatar;
    private TextView tvNickname, tvUserId;
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
        setupMenuClicks();
        loadUserProfile();
    }

    private void initViews() {
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);

        llFavorite = findViewById(R.id.ll_favorite);
        llMessageRecord = findViewById(R.id.ll_message_record);
        llPostRecord = findViewById(R.id.ll_post_record);
        llTipRecord = findViewById(R.id.ll_tip_record);

        llWallet = findViewById(R.id.ll_wallet);
        llEditProfile = findViewById(R.id.ll_edit_profile);
        llLogout = findViewById(R.id.ll_logout);
        llReportRecord = findViewById(R.id.ll_report_record);

        ivAvatar = findViewById(R.id.iv_avatar);
        tvNickname = findViewById(R.id.tv_nickname);
        tvUserId = findViewById(R.id.tv_user_id);
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
        User currentUser = SharedPrefsHelper.getInstance().getUser();
        if (currentUser == null) {
            Toast.makeText(this, "用户信息加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 设置昵称
        String nickname = currentUser.getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            tvNickname.setText(nickname);
        } else {
            tvNickname.setText("用户" + currentUser.getUserId());
        }

        // 设置用户ID
        tvUserId.setText("ID: " + currentUser.getUserId());

        // 设置头像
        if (!TextUtils.isEmpty(currentUser.getAvatarUrl())) {
            Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        }
    }

    /**
     * 设置菜单项点击事件
     */
    private void setupMenuClicks() {
        // 收藏的帖子
        if (llFavorite != null) {
            llFavorite.setOnClickListener(v -> {
                Toast.makeText(this, "收藏功能开发中", Toast.LENGTH_SHORT).show();
            });
        }

        // 留言记录
        if (llMessageRecord != null) {
            llMessageRecord.setOnClickListener(v -> {
                Toast.makeText(this, "留言记录功能开发中", Toast.LENGTH_SHORT).show();
            });
        }

        // 发帖记录
        if (llPostRecord != null) {
            llPostRecord.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, UserPostsActivity.class);
                startActivity(intent);
            });
        }

        // 打赏记录
        if (llTipRecord != null) {
            llTipRecord.setOnClickListener(v -> {
                Toast.makeText(this, "打赏记录功能开发中", Toast.LENGTH_SHORT).show();
            });
        }

        // 收益钱包
        if (llWallet != null) {
            llWallet.setOnClickListener(v -> {
                Toast.makeText(this, "钱包功能开发中", Toast.LENGTH_SHORT).show();
            });
        }

        // 修改个人资料
        if (llEditProfile != null) {
            llEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, EditprofileActivity.class);
                startActivity(intent);
            });
        }

        // 退出登录
        if (llLogout != null) {
            llLogout.setOnClickListener(v -> {
                profileViewModel.logout();
                Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // 举报记录
        if (llReportRecord != null) {
            llReportRecord.setOnClickListener(v -> {
                Toast.makeText(this, "举报记录功能开发中", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
