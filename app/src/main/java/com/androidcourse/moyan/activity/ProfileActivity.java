package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.androidcourse.moyan.R;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        profileViewModel = new ProfileViewModel();
        initViews();
        setupBottomNavigation();
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
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, InteractionActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }
}