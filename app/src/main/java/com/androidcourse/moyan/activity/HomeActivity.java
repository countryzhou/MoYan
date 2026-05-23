package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.adapter.NewsAdapter;
import com.androidcourse.moyan.adapter.TrendCardAdapter;
import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.TrendCard;
import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivAvatar;
    private RecyclerView rvTrendCards;
    private RecyclerView rvNewsList;
    private FloatingActionButton fabWrite;
    private LinearLayout dotIndicator;
    private LinearLayout navHome, navExplore, navMessages, navProfile;

    private TrendCardAdapter trendCardAdapter;
    private NewsAdapter newsAdapter;
    private HomeViewModel homeViewModel;
    private SharedPrefsHelper sharedPrefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        homeViewModel = new HomeViewModel();

        initViews();
        setupListeners();
        loadData();
        showWelcomeMessage();
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

        ivAvatar.setOnClickListener(v -> {
            if (sharedPrefsHelper.isGuestMode()) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        fabWrite.setOnClickListener(v -> {
            if (!checkLogin()) return;
            Intent intent = new Intent(HomeActivity.this, CreatepostActivity.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> refreshData());
        navExplore.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, InteractionActivity.class));
        });
        navMessages.setOnClickListener(v -> {
            if (sharedPrefsHelper.isGuestMode()) {
                Toast.makeText(this, "请先登录后再查看消息", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(HomeActivity.this, MessageActivity.class));
        });
        navProfile.setOnClickListener(v -> {
            if (sharedPrefsHelper.isGuestMode() || !sharedPrefsHelper.isLogin()) {
                if (!checkLogin()) return;
            }
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });
    }

    private boolean checkLogin() {
        if (!sharedPrefsHelper.isLogin()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            return false;
        }
        return true;
    }

    private void showWelcomeMessage() {
        String message;
        if (sharedPrefsHelper.isLogin()) {
            User user = sharedPrefsHelper.getUser();
            if (user != null && !TextUtils.isEmpty(user.getNickname())) {
                message = "欢迎回来，" + user.getNickname() + "！";
            } else {
                message = "欢迎回来！";
            }
        } else if (sharedPrefsHelper.isGuestMode()) {
            message = "游客模式，登录后可参与互动";
        } else {
            message = "欢迎来到陌言";
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void loadData() {
        homeViewModel.loadTrendCards(new HomeViewModel.TrendCardCallback() {
            @Override
            public void onSuccess(List<TrendCard> cards) {
                displayTrendCards(cards);
            }

            @Override
            public void onError(String error) {
                Log.e("HomeActivity", "加载趋势卡片失败: " + error);
            }
        });

        homeViewModel.loadNewsList(new HomeViewModel.NewsListCallback() {
            @Override
            public void onSuccess(List<NewsItem> news) {
                displayNewsList(news);
            }

            @Override
            public void onError(String error) {
                Log.e("HomeActivity", "加载新闻列表失败: " + error);
            }
        });
    }

    private void displayTrendCards(List<TrendCard> cards) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrendCards.setLayoutManager(layoutManager);

        trendCardAdapter = new TrendCardAdapter(this, cards, trendCard -> {
            Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
            intent.putExtra("post_id", trendCard.getPostId());
            startActivity(intent);
        });

        rvTrendCards.setAdapter(trendCardAdapter);
        rvTrendCards.post(() -> createDotIndicators(cards.size()));
        rvTrendCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateDotByScroll();
                }
            }
        });
    }

    private void displayNewsList(List<NewsItem> news) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNewsList.setLayoutManager(layoutManager);

        newsAdapter = new NewsAdapter(this, news, newsItem -> {
            Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
            intent.putExtra("post_id", newsItem.getId());
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
        loadData();
    }
}