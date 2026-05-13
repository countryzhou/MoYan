package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.repository.UserRepository;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 编辑个人资料页面
 */
public class EditprofileActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private EditText etNickname;
    private TextView tvSave;
    private ImageView ivBack;

    private User currentUser;
    private UserRepository userRepository;
    private Uri selectedAvatarUri;

    // 使用 Activity Result API 替代 startActivityForResult
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedAvatarUri = result.getData().getData();
                        ivAvatar.setImageURI(selectedAvatarUri);
                    }
                }
            }
    );

    // 或者使用更简洁的 Lambda 表达式版本
    /*
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAvatarUri = result.getData().getData();
                    ivAvatar.setImageURI(selectedAvatarUri);
                }
            }
    );
    */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        userRepository = new UserRepository();
        initViews();
        loadUserInfo();
        setupListeners();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        etNickname = findViewById(R.id.et_nickname);
        tvSave = findViewById(R.id.tv_save);
        ivBack = findViewById(R.id.iv_back);
    }

    private void loadUserInfo() {
        currentUser = SharedPrefsHelper.getInstance().getUser();
        if (currentUser != null) {
            etNickname.setText(currentUser.getNickname());
            if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                        .load(currentUser.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .into(ivAvatar);
            }
        }
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        // 选择头像 - 使用新的 API
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // 保存修改
        tvSave.setOnClickListener(v -> saveProfile());
    }

    // 移除 onActivityResult 方法，不再需要

    private void saveProfile() {
        String nickname = etNickname.getText().toString().trim();

        if (nickname.isEmpty()) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nickname.length() < 2 || nickname.length() > 20) {
            Toast.makeText(this, "昵称长度需在2-20位之间", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: 上传头像到服务器
        // 保存昵称
        User updatedUser = currentUser;
        updatedUser.setNickname(nickname);

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}