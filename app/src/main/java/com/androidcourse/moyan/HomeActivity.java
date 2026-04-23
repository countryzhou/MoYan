package com.androidcourse.moyan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvTrendCards;
    private LinearLayout dotIndicator;
    private LinearLayout navHome, navExplore, navMessages, navProfile;
    private FloatingActionButton fabWrite;

    private TrendCardAdapter adapter;
    private List<TrendCardItem> trendList;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int currentPosition = 0;
    private boolean isScrolling = false;
    private PagerSnapHelper snapHelper;

    // 内部数据类
    private static class TrendCardItem {
        String title, author, commentCount, time;
        int imageRes;
        TrendCardItem(String title, String author, String commentCount, String time, int imageRes) {
            this.title = title;
            this.author = author;
            this.commentCount = commentCount;
            this.time = time;
            this.imageRes = imageRes;
        }
    }

    // 内部 Adapter - 实现无限循环
    private class TrendCardAdapter extends RecyclerView.Adapter<TrendCardAdapter.ViewHolder> {
        private final List<TrendCardItem> list;
        private final int REAL_COUNT;

        TrendCardAdapter(List<TrendCardItem> list) {
            this.list = list;
            this.REAL_COUNT = list == null ? 0 : list.size();
        }

        @Override
        public int getItemCount() {
            // 返回一个很大的数，实现无限循环
            return REAL_COUNT == 0 ? 0 : Integer.MAX_VALUE;
        }

        @Override
        public int getItemViewType(int position) {
            return position % REAL_COUNT;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trend_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int realPosition = position % REAL_COUNT;
            TrendCardItem item = list.get(realPosition);
            holder.tvTitle.setText(item.title);
            holder.tvAuthor.setText(item.author);
            holder.tvCommentCount.setText(item.commentCount);
            holder.tvTime.setText(item.time);
            holder.ivImage.setImageResource(item.imageRes);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_title", item.title);
                intent.putExtra("post_author", item.author);
                startActivity(intent);
            });
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvAuthor, tvCommentCount, tvTime;
            ImageView ivImage;
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_trend_title);
                tvAuthor = itemView.findViewById(R.id.tv_author);
                tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
                tvTime = itemView.findViewById(R.id.tv_time);
                ivImage = itemView.findViewById(R.id.iv_trend_image);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadMockData();
        setupTrendCarousel();
        setupBottomNavigation();
        setupFab();
        setupNewsCardClickListeners();
    }

    private void initViews() {
        rvTrendCards = findViewById(R.id.rv_trend_cards);
        dotIndicator = findViewById(R.id.dot_indicator);
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
        fabWrite = findViewById(R.id.fab_write);
    }

    private void loadMockData() {
        trendList = new ArrayList<>();
        trendList.add(new TrendCardItem("全新BMW M2震撼登场，性能再升级", "BMW官方", "128", "2小时前", R.drawable.img_car_placeholder));
        trendList.add(new TrendCardItem("特斯拉Cybertruck终于交付！", "特斯拉中国", "256", "5小时前", R.drawable.img_car_placeholder));
        trendList.add(new TrendCardItem("奔驰发布全新E级，科技感十足", "奔驰汽车", "89", "昨天", R.drawable.img_car_placeholder));
        trendList.add(new TrendCardItem("奥迪Q6 e-tron亮相，纯电新选择", "奥迪官方", "67", "昨天", R.drawable.img_car_placeholder));
    }

    private void setupTrendCarousel() {
        if (trendList == null || trendList.isEmpty()) {
            if (dotIndicator != null) {
                dotIndicator.setVisibility(View.GONE);
            }
            return;
        }

        // 使用 LinearLayoutManager
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrendCards.setLayoutManager(layoutManager);

        adapter = new TrendCardAdapter(trendList);
        rvTrendCards.setAdapter(adapter);

        // 使用 PagerSnapHelper 实现一次滑动切换一个卡片
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvTrendCards);

        // 添加 item 装饰器来增加左右间距，使居中效果更好
        rvTrendCards.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int totalCount = adapter.getItemCount();

                // 第一个 item 左边距，最后一个 item 右边距
                if (position == 0) {
                    outRect.left = (parent.getWidth() - view.getWidth()) / 2;
                }
                if (position == totalCount - 1) {
                    outRect.right = (parent.getWidth() - view.getWidth()) / 2;
                }
            }
        });

        addDotIndicators(trendList.size());

        // 监听滚动，更新指示点和位置
        rvTrendCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateCurrentPosition();
                    isScrolling = false;
                } else {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isScrolling) {
                    updateCurrentPosition();
                }
            }
        });

        // 初始化：滚动到中间位置，使第一个卡片居中
        rvTrendCards.post(() -> {
            // 计算中间位置（无限循环的中间）
            int middlePosition = Integer.MAX_VALUE / 2;
            // 调整到第一个卡片的位置
            int targetPosition = middlePosition - (middlePosition % trendList.size());

            // 平滑滚动到目标位置
            layoutManager.scrollToPosition(targetPosition);

            // 等待布局完成后精确居中
            rvTrendCards.postDelayed(() -> {
                View firstChild = layoutManager.findViewByPosition(targetPosition);
                if (firstChild != null) {
                    int targetScroll = (rvTrendCards.getWidth() - firstChild.getWidth()) / 2 - firstChild.getLeft();
                    rvTrendCards.smoothScrollBy(targetScroll, 0);
                }
                updateCurrentPosition();
            }, 100);
        });
    }

    private void updateCurrentPosition() {
        if (snapHelper == null || rvTrendCards.getLayoutManager() == null) return;

        View snapView = snapHelper.findSnapView(rvTrendCards.getLayoutManager());
        if (snapView != null) {
            int position = rvTrendCards.getLayoutManager().getPosition(snapView);
            if (position != RecyclerView.NO_POSITION && trendList != null && !trendList.isEmpty()) {
                int realPosition = position % trendList.size();
                if (realPosition != currentPosition) {
                    currentPosition = realPosition;
                    updateDotsStyle(currentPosition);
                }
            }
        }
    }

    private void addDotIndicators(int count) {
        if (dotIndicator == null) return;
        dotIndicator.removeAllViews();
        dotIndicator.setVisibility(View.VISIBLE);

        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == 0 ? 8 : 6, i == 0 ? 8 : 6);
            params.setMargins(0, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == 0 ? R.drawable.bg_dot_active : R.drawable.bg_dot_inactive);
            dotIndicator.addView(dot);
        }
    }

    private void updateDotsStyle(int activePosition) {
        if (dotIndicator == null) return;
        for (int i = 0; i < dotIndicator.getChildCount(); i++) {
            View dot = dotIndicator.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            if (i == activePosition) {
                params.width = 8;
                params.height = 8;
                dot.setBackgroundResource(R.drawable.bg_dot_active);
            } else {
                params.width = 6;
                params.height = 6;
                dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            }
            dot.setLayoutParams(params);
        }
    }

    private void setupBottomNavigation() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                // 滚动到第一个卡片
                if (rvTrendCards.getLayoutManager() != null && trendList != null && !trendList.isEmpty()) {
                    int targetPosition = Integer.MAX_VALUE / 2;
                    targetPosition = targetPosition - (targetPosition % trendList.size());
                    rvTrendCards.smoothScrollToPosition(targetPosition);
                    handler.postDelayed(this::updateCurrentPosition, 300);
                }
                Toast.makeText(this, "已在首页", Toast.LENGTH_SHORT).show();
            });
        }

        if (navExplore != null) {
            navExplore.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, InteractionActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navMessages != null) {
            navMessages.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void setupFab() {
        if (fabWrite != null) {
            fabWrite.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, CreatepostActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    private void setupNewsCardClickListeners() {
        View newsItem1 = findViewById(R.id.newsItem1);
        View newsItem2 = findViewById(R.id.newsItem2);
        View newsItem3 = findViewById(R.id.newsItem3);

        if (newsItem1 != null) {
            newsItem1.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_title", "新能源汽车市场持续升温");
                startActivity(intent);
            });
        }

        if (newsItem2 != null) {
            newsItem2.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_title", "智能驾驶技术迎来新突破");
                startActivity(intent);
            });
        }

        if (newsItem3 != null) {
            newsItem3.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_title", "传统车企加速电动化转型");
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}