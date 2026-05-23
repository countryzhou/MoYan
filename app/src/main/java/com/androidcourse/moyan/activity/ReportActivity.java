//调用方式new
//Intent intent = new Intent(context, ReportActivity.class);
//intent.putExtra("target_type", 1);  // 1=帖子，2=回复
//intent.putExtra("target_id", postId);
//startActivity(intent);

package com.androidcourse.moyan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.network.PostNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvSubmit;
    private TextView tvTargetHint;
    private RadioGroup rgReason;
    private EditText etCustomReason;
    private RadioButton rbOther;

    private int targetType;  // 1=帖子，2=回复
    private int targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // 获取传入参数
        targetType = getIntent().getIntExtra("target_type", 1);
        targetId = getIntent().getIntExtra("target_id", -1);

        if (targetId == -1) {
            Toast.makeText(this, "举报对象无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        updateTargetHint();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvSubmit = findViewById(R.id.tv_submit);
        tvTargetHint = findViewById(R.id.tv_target_hint);
        rgReason = findViewById(R.id.rg_reason);
        etCustomReason = findViewById(R.id.et_custom_reason);
        rbOther = findViewById(R.id.rb_other);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        tvSubmit.setOnClickListener(v -> submitReport());

        // 当选择"其他"时显示自定义输入框
        rgReason.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_other) {
                etCustomReason.setVisibility(View.VISIBLE);
            } else {
                etCustomReason.setVisibility(View.GONE);
                etCustomReason.setText("");
            }
        });
    }

    private void updateTargetHint() {
        String targetText = targetType == 1 ? "帖子" : "回复";
        tvTargetHint.setText("举报对象：" + targetText + " (ID: " + targetId + ")");
    }

    private void submitReport() {
        int selectedId = rgReason.getCheckedRadioButtonId();
        String reason;

        if (selectedId == R.id.rb_other) {
            reason = etCustomReason.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(this, "请填写举报原因", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            RadioButton selectedButton = findViewById(selectedId);
            if (selectedButton == null) {
                Toast.makeText(this, "请选择举报原因", Toast.LENGTH_SHORT).show();
                return;
            }
            reason = selectedButton.getText().toString();
        }

        int reporterId = SharedPrefsHelper.getInstance().getUserId();
        if (reporterId <= 0) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示加载状态
        tvSubmit.setEnabled(false);
        tvSubmit.setText("提交中...");

        // 发送举报请求
        new Thread(() -> {
            String response = PostNetworkManager.getInstance()
                    .report(reporterId, targetType, targetId, reason);

            runOnUiThread(() -> {
                tvSubmit.setEnabled(true);
                tvSubmit.setText("提交");

                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("code") == 0) {
                        Toast.makeText(ReportActivity.this, "举报成功，我们会尽快处理", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String msg = json.optString("msg", "举报失败");
                        Toast.makeText(ReportActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ReportActivity.this, "举报失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}