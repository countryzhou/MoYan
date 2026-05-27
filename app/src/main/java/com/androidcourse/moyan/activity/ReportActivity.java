package com.androidcourse.moyan.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.network.PostNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etReason;
    private Button btnSubmit;

    private int targetType; // 1:帖子 2:回复
    private int targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        targetType = getIntent().getIntExtra("target_type", 1);
        targetId = getIntent().getIntExtra("target_id", -1);

        if (targetId == -1) {
            Toast.makeText(this, "举报对象无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivBack = findViewById(R.id.ivBack);
        etReason = findViewById(R.id.etReason);
        btnSubmit = findViewById(R.id.btnSubmit);

        ivBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {
        String reason = etReason.getText().toString().trim();

        if (TextUtils.isEmpty(reason)) {
            Toast.makeText(this, "请填写举报原因", Toast.LENGTH_SHORT).show();
            return;
        }

        int reporterId = SharedPrefsHelper.getInstance().getUserId();
        if (reporterId <= 0) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("提交中...");

        new Thread(() -> {
            String response = PostNetworkManager.getInstance().report(
                    reporterId,
                    targetType,
                    targetId,
                    reason
            );

            runOnUiThread(() -> {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("提交举报");

                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("code") == 0) {
                        Toast.makeText(ReportActivity.this, "举报成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String msg = json.optString("msg", "举报失败");
                        Toast.makeText(ReportActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ReportActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}