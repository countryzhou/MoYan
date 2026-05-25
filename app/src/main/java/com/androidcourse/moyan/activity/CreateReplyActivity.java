package com.androidcourse.moyan.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
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
    private TextView tvCharCount;

    private PostDetailViewModel viewModel;
    private int postId;
    private static final int MAX_CHAR_COUNT = 1000;

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
        tvCharCount = findViewById(R.id.tvCharCount);

        updateCharCount();
    }

    private void setupListeners() {
        sendButtonContainer.setOnClickListener(v -> submitReply());

        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharCount();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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

    private void submitReply() {
        String content = commentEditText.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入回复内容", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.length() > MAX_CHAR_COUNT) {
            Toast.makeText(this, "回复内容不能超过1000字", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAnonymous = anonymousCheckBox.isChecked();

        viewModel.submitReply(postId, content, isAnonymous, new PostDetailViewModel.SubmitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CreateReplyActivity.this, "回复成功，等待审核通过即可公开显示", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CreateReplyActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCharCount() {
        if (commentEditText == null || tvCharCount == null) return;

        int currentLength = commentEditText.getText().length();
        tvCharCount.setText("当前字数 " + currentLength + "/" + MAX_CHAR_COUNT + " 字");

        if (currentLength > MAX_CHAR_COUNT) {
            tvCharCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (currentLength > MAX_CHAR_COUNT * 0.9) {
            tvCharCount.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvCharCount.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardUtils.hideKeyboard(this);
    }
}
