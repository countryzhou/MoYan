package com.androidcourse.moyan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.androidcourse.moyan.R;

import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.repository.UserRepository;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 用户主页（查看他人主页）
 * 通过userId搜索或从帖子头像点击进入
 */
public class UserProfileActivity extends AppCompatActivity {

    private ImageView ivBack;
    private CircleImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvBio;
    private TextView tvStats;
    private int targetUserId;
    private UserRepository userRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 临时使用 activity_profile 布局，后续需要创建 activity_user_profile.xml
        setContentView(R.layout.activity_profile);
        
        userRepository = new UserRepository();
        targetUserId = getIntent().getIntExtra("user_id", -1);
        
        if (targetUserId == -1) {
            Toast.makeText(this, "无效的用户ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupListeners();
        loadUserInfo();
    }
    
    private void initViews() {
        // 根据 activity_profile.xml 中的实际 ID 进行初始化
        ivAvatar = findViewById(R.id.iv_avatar);
        tvNickname = findViewById(R.id.tv_nickname);
        
        // activity_profile.xml 中没有 iv_back、tv_bio、tv_stats
        // 只初始化存在的控件，其他功能暂时注释
        ivBack = null;      // activity_profile.xml 中没有返回按钮
        tvBio = null;       // 用 tv_user_id 替代
        tvStats = null;     // 暂时没有对应的统计信息控件
    }
    
    private void setupListeners() {
        // activity_profile.xml 中没有返回按钮，使用系统返回键
        // 用户可以通过系统返回键或手势返回
    }
    
    private void loadUserInfo() {
        userRepository.getUserInfo(targetUserId, new UserRepository.RepositoryCallback<User>() {
            @Override
            public void onResult(User user) {
                runOnUiThread(() -> {
                    displayUserInfo(user);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(UserProfileActivity.this, "加载失败: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    private void displayUserInfo(User user) {
        if (user == null) return;
        
        tvNickname.setText(user.getNickname());
        
        // activity_profile.xml 中有 tv_user_id，可以显示用户ID
        TextView tvUserId = findViewById(R.id.tv_user_id);
        if (tvUserId != null) {
            tvUserId.setText("ID: " + user.getUserId());
        }
        
        if (!TextUtils.isEmpty(user.getAvatarUrl())) {
            Glide.with(this)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        }
    }
}