package com.androidcourse.moyan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.utils.KeyboardUtils;
import com.androidcourse.moyan.viewmodel.PostDetailViewModel;

/**
 * 发表回复页面
 */
public class CreateReplyActivity extends AppCompatActivity {

    private EditText commentEditText;
    private ImageView imageButton;
    private ImageView keyboardButton;
    private ImageView addButton;
    private ImageView emotionButton;
    private TextView sendTextView;
    private LinearLayout sendButtonContainer;
    private CheckBox anonymousCheckBox;
    private LinearLayout anonymousContainer;

    private PostDetailViewModel viewModel;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createreply);

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId == -1) {
            Toast.makeText(this, "参数错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new PostDetailViewModel();
        initViews();
        setupListeners();
        setupBackHandler();

        // 自动弹出键盘
        commentEditText.postDelayed(() -> {
            KeyboardUtils.showKeyboard(CreateReplyActivity.this, commentEditText);
        }, 200);
    }

    private void initViews() {
        commentEditText = findViewById(R.id.commentEditText);
        imageButton = findViewById(R.id.imageButton);
        keyboardButton = findViewById(R.id.keyboardButton);
        addButton = findViewById(R.id.addButton);
        emotionButton = findViewById(R.id.emotionButton);
        sendTextView = findViewById(R.id.sendTextView);
        sendButtonContainer = findViewById(R.id.sendButtonContainer);
        anonymousCheckBox = findViewById(R.id.anonymousCheckBox);
        anonymousContainer = findViewById(R.id.anonymousContainer);
    }

    private void setupListeners() {
        // 发送按钮
        sendButtonContainer.setOnClickListener(v -> submitComment());

        // 匿名复选框
        anonymousCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 可以在这里添加匿名状态变化的处理
        });

        // 图片按钮（暂时提示功能未实现）
        imageButton.setOnClickListener(v -> {
            Toast.makeText(this, "图片功能暂未实现", Toast.LENGTH_SHORT).show();
        });

        // 键盘按钮
        keyboardButton.setOnClickListener(v -> {
            KeyboardUtils.toggleKeyboard(this, commentEditText);
        });

        // 添加按钮（暂时提示功能未实现）
        addButton.setOnClickListener(v -> {
            Toast.makeText(this, "添加功能暂未实现", Toast.LENGTH_SHORT).show();
        });

        // 表情按钮（暂时提示功能未实现）
        emotionButton.setOnClickListener(v -> {
            Toast.makeText(this, "表情功能暂未实现", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupBackHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void submitComment() {
        String content = commentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAnonymous = anonymousCheckBox.isChecked();

        viewModel.submitReply(this, postId, content, isAnonymous, new PostDetailViewModel.SubmitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CreateReplyActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CreateReplyActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 隐藏键盘
        KeyboardUtils.hideKeyboard(this);
    }
}
