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
    private TextView sendTextView;
    private LinearLayout sendButtonContainer;
    private CheckBox anonymousCheckBox;

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
        sendTextView = findViewById(R.id.sendTextView);
        sendButtonContainer = findViewById(R.id.sendButtonContainer);
        anonymousCheckBox = findViewById(R.id.anonymousCheckBox);
    }

    private void setupListeners() {
        sendButtonContainer.setOnClickListener(v -> submitReply());

        // 可选：其他按钮（图片、键盘、添加、表情）可以保持原有 Toast 提示
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

    private void submitReply() {
        String content = commentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入回复内容", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAnonymous = anonymousCheckBox.isChecked();

        viewModel.submitReply(this, postId, content, isAnonymous, new PostDetailViewModel.SubmitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CreateReplyActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
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
        KeyboardUtils.hideKeyboard(this);
    }
}