package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.adapter.*;
import com.androidcourse.moyan.model.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // UI组件
    private EditText etSearch;
    private ImageView ivAvatar;
    private RecyclerView rvTrendCards;
    private RecyclerView rvNewsList;
    private FloatingActionButton fabWrite;
    private LinearLayout dotIndicator;

    // 底部导航栏
    private LinearLayout navHome, navExplore, navMessages, navProfile;

    // 适配器
    private TrendCardAdapter trendCardAdapter;
    private NewsAdapter newsAdapter;

    // 数据源
    private List<TrendCard> trendCardList;
    private List<NewsItem> newsList;

    private Handler mainHandler;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupListeners();

        // 先显示默认数据
        displayTrendCards(TrendCard.getDefaultTrendCards());
        displayNewsList(NewsItem.getDefaultNewsList());

        // 从服务器加载数据
        loadDataFromServer();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        ivAvatar = findViewById(R.id.iv_avatar);
        rvTrendCards = findViewById(R.id.rv_trend_cards);
        rvNewsList = findViewById(R.id.rv_news_list);
        fabWrite = findViewById(R.id.fab_write);
        dotIndicator = findViewById(R.id.dot_indicator);

        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupListeners() {
        etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        etSearch.setFocusable(false);

        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        fabWrite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EditprofileActivity.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> refreshData());
        navExplore.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, InteractionActivity.class));
            finish();
        });
        navMessages.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MessageActivity.class));
            finish();
        });
        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        });
    }

    private void loadDataFromServer() {
        // 加载趋势卡片
        TrendCard.fetchTrendCards(currentUserId, new TrendCard.TrendCardCallback() {
            @Override
            public void onSuccess(List<TrendCard> cards) {
                if (cards != null && !cards.isEmpty()) {
                    displayTrendCards(cards);
                    Log.d("HomeActivity", "趋势卡片加载成功，数量：" + cards.size());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeActivity", "加载趋势卡片失败: " + error);
                // 失败时保持默认数据
            }
        });

        // 加载新闻列表
        NewsItem.fetchNewsList(currentUserId, 1, 20, new NewsItem.NewsListCallback() {
            @Override
            public void onSuccess(List<NewsItem> news) {
                if (news != null && !news.isEmpty()) {
                    displayNewsList(news);
                    Log.d("HomeActivity", "新闻列表加载成功，数量：" + news.size());
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeActivity", "加载新闻列表失败: " + error);
                // 失败时保持默认数据
            }
        });
    }

    private void displayTrendCards(List<TrendCard> cards) {
        trendCardList = cards;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrendCards.setLayoutManager(layoutManager);

        trendCardAdapter = new TrendCardAdapter(this, trendCardList, trendCard -> {
            Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
            intent.putExtra("post_id", trendCard.getPostId());
            intent.putExtra("news_title", trendCard.getTitle());
            startActivity(intent);
        });

        rvTrendCards.setAdapter(trendCardAdapter);

        // 创建指示点
        rvTrendCards.post(() -> createDotIndicators(cards.size()));

        // 添加滚动监听
        rvTrendCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateDotByScroll();
                }
            }
        });
    }

    private void displayNewsList(List<NewsItem> news) {
        newsList = news;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNewsList.setLayoutManager(layoutManager);

        newsAdapter = new NewsAdapter(this, newsList, newsItem -> {
            Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
            intent.putExtra("post_id", newsItem.getId());
            intent.putExtra("news_title", newsItem.getTitle());
            intent.putExtra("news_author", newsItem.getAuthor());
            intent.putExtra("news_comment_count", newsItem.getCommentCount());
            startActivity(intent);
        });

        rvNewsList.setAdapter(newsAdapter);
    }

    private void createDotIndicators(int count) {
        if (count <= 1) {
            dotIndicator.setVisibility(View.GONE);
            return;
        }

        dotIndicator.removeAllViews();

        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(8), dpToPx(8));
            params.setMarginEnd(dpToPx(8));
            params.setMarginStart(dpToPx(8));
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            dotIndicator.addView(dot);
        }

        dotIndicator.setVisibility(View.VISIBLE);
        selectDot(0);
    }

    private void selectDot(int position) {
        if (dotIndicator == null || dotIndicator.getChildCount() == 0) return;

        for (int i = 0; i < dotIndicator.getChildCount(); i++) {
            View dot = dotIndicator.getChildAt(i);
            if (i == position) {
                dot.setBackgroundResource(R.drawable.bg_dot_active);
            } else {
                dot.setBackgroundResource(R.drawable.bg_dot_inactive);
            }
        }
    }

    private void updateDotByScroll() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvTrendCards.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        if (firstVisiblePosition != RecyclerView.NO_POSITION) {
            selectDot(firstVisiblePosition);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void refreshData() {
        Toast.makeText(this, "刷新中...", Toast.LENGTH_SHORT).show();
        loadDataFromServer();
    }
}