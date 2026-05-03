package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidcourse.moyan.R;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Intent intent = new Intent(MessageActivity.this, InteractionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
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
        list.add(new PrivateMessageItem("新浪新闻", "特朗普认为是中国促使伊朗进行谈判...", "17:05"));
        list.add(new PrivateMessageItem("活动通知", "超话社区：亲爱的@用户...", "3-27"));
        list.add(new PrivateMessageItem("服务通知", "超话社区：#时光代理人...", "25-2-6"));
        return list;
    }

    private void showComingSoonToast() {
        android.widget.Toast.makeText(this, "功能开发中，敬请期待",
                android.widget.Toast.LENGTH_SHORT).show();
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
                android.widget.Toast.makeText(v.getContext(),
                        "进入与" + item.name + "的聊天", android.widget.Toast.LENGTH_SHORT).show();
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