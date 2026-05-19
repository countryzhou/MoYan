package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.androidcourse.moyan.R;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.utils.SharedPrefsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息页面
 */
public class MessageActivity extends AppCompatActivity {

    private LinearLayout entryLike;
    private LinearLayout entryMention;
    private LinearLayout entryComment;
    private RecyclerView rvPrivateMessages;
    private PrivateMessageAdapter privateMessageAdapter;
    private LinearLayout navHome, navExplore, navMessages, navProfile;

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
        setContentView(R.layout.activity_message);

        initViews();
        setupListeners();
        setupPrivateMessageList();
    }

    private void initViews() {
        entryLike = findViewById(R.id.entry_like);
        entryMention = findViewById(R.id.entry_mention);
        entryComment = findViewById(R.id.entry_comment);
        rvPrivateMessages = findViewById(R.id.rv_private_messages);
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupListeners() {
        if (entryLike != null) entryLike.setOnClickListener(v -> showComingSoonToast());
        if (entryMention != null) entryMention.setOnClickListener(v -> showComingSoonToast());
        if (entryComment != null) entryComment.setOnClickListener(v -> showComingSoonToast());

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(MessageActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Intent intent = new Intent(MessageActivity.this, InteractionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransitionCompat(android.R.anim.fade_in, android.R.anim.fade_out);
            });
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

    private void setupPrivateMessageList() {
        if (rvPrivateMessages != null) {
            rvPrivateMessages.setLayoutManager(new LinearLayoutManager(this));
            privateMessageAdapter = new PrivateMessageAdapter(getMockPrivateMessages());
            rvPrivateMessages.setAdapter(privateMessageAdapter);
        }
    }

    private List<PrivateMessageItem> getMockPrivateMessages() {
        List<PrivateMessageItem> list = new ArrayList<>();
        list.add(new PrivateMessageItem("系统通知", "欢迎使用陌言社交App！", "10:30"));
        list.add(new PrivateMessageItem("点赞助手", "你的帖子获得了10个新赞", "昨天"));
        list.add(new PrivateMessageItem("评论提醒", "张三评论了你的帖子", "星期一"));
        list.add(new PrivateMessageItem("私信消息", "李四给你发了一条消息", "2024-01-15"));
        return list;
    }

    private void showComingSoonToast() {
        Toast.makeText(this, "功能开发中，敬请期待", Toast.LENGTH_SHORT).show();
    }

    private static class PrivateMessageItem {
        String name;
        String content;
        String time;

        PrivateMessageItem(String name, String content, String time) {
            this.name = name;
            this.content = content;
            this.time = time;
        }
    }

    private static class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.ViewHolder> {

        private List<PrivateMessageItem> list;

        PrivateMessageAdapter(List<PrivateMessageItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_private_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PrivateMessageItem item = list.get(position);
            holder.tvName.setText(item.name);
            holder.tvContent.setText(item.content);
            holder.tvTime.setText(item.time);
            holder.itemView.setOnClickListener(v -> {

                // TODO: 跳转到聊天详情页面
                Toast.makeText(v.getContext(),
                        "进入与" + item.name + "的聊天", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivAvatar;
            TextView tvName;
            TextView tvContent;
            TextView tvTime;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
                tvName = itemView.findViewById(R.id.tv_name);
                tvContent = itemView.findViewById(R.id.tv_content);
                tvTime = itemView.findViewById(R.id.tv_time);
            }
        }
    }
}