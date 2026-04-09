package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class MessageActivity extends AppCompatActivity {

    // 互动入口
    private LinearLayout entryLike;
    private LinearLayout entryMention;
    private LinearLayout entryComment;

    // 私信列表
    private RecyclerView rvPrivateMessages;
    private PrivateMessageAdapter privateMessageAdapter;

    // 底部导航
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

        // 互动入口
        entryLike = findViewById(R.id.entry_like);
        entryMention = findViewById(R.id.entry_mention);
        entryComment = findViewById(R.id.entry_comment);

        // 私信列表
        rvPrivateMessages = findViewById(R.id.rv_private_messages);

        // 底部导航
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupListeners() {

        // 互动入口点击事件
        if (entryLike != null) {
            entryLike.setOnClickListener(v -> showComingSoonToast());
        }

        if (entryMention != null) {
            entryMention.setOnClickListener(v -> showComingSoonToast());
        }

        if (entryComment != null) {
            entryComment.setOnClickListener(v -> showComingSoonToast());
        }

        // 底部导航栏点击事件
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

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                // 当前已在消息页，不做处理
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
        list.add(new PrivateMessageItem("活动通知", "超话社区：亲爱的@用户780280644...", "3-27"));
        list.add(new PrivateMessageItem("服务通知", "超话社区：#时光代理人[超话]#本...", "25-2-6"));
        list.add(new PrivateMessageItem("留言板", "超话社区：亲爱的@用户780280...", "23-3-27"));
        list.add(new PrivateMessageItem("群推荐", "加入感兴趣的粉丝群，开启热聊...", "22-11-21"));
        list.add(new PrivateMessageItem("微博安全中心", "欢迎来到微博~你尚未设置密码...", "22-11-21"));
        return list;
    }

    private void showComingSoonToast() {
        android.widget.Toast.makeText(this, "功能开发中，敬请期待", android.widget.Toast.LENGTH_SHORT).show();
    }

    // 私信数据模型
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

    // 私信列表适配器
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

            // 私信条目点击事件
            holder.itemView.setOnClickListener(v -> {
                android.widget.Toast.makeText(v.getContext(), "进入与" + item.name + "的聊天", android.widget.Toast.LENGTH_SHORT).show();
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