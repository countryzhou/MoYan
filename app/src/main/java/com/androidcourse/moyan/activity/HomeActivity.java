package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.adapter.HomeUnifiedAdapter;
import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.TrendCard;
import com.androidcourse.moyan.model.User;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView etSearch;
    private ImageView ivAvatar;
    private RecyclerView rvHomeUnified;
    private FloatingActionButton fabWrite;
    private LinearLayout navHome, navExplore, navMessages, navProfile;
    private SwipeRefreshLayout swipeRefresh;

    private HomeUnifiedAdapter unifiedAdapter;
    private HomeViewModel homeViewModel;
    private SharedPrefsHelper sharedPrefsHelper;

    private List<TrendCard> trendCards = new ArrayList<>();
    private List<NewsItem> newsList = new ArrayList<>();

    private boolean isLoadingMore = false;
    private boolean hasMoreData = true;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        homeViewModel = new HomeViewModel();

        initViews();
        setupListeners();
        setupRecyclerView();
        loadData();
        showWelcomeMessage();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        ivAvatar = findViewById(R.id.iv_avatar);
        rvHomeUnified = findViewById(R.id.rv_home_unified);
        fabWrite = findViewById(R.id.fab_write);
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
        swipeRefresh = findViewById(R.id.swipe_refresh);
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

        swipeRefresh.setOnRefreshListener(() -> {
            refreshData();
        });
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        rvHomeUnified.setLayoutManager(layoutManager);

        unifiedAdapter = new HomeUnifiedAdapter(this, trendCards, newsList, new HomeUnifiedAdapter.OnItemClickListener() {
            @Override
            public void onTrendCardClick(TrendCard trendCard) {
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_id", trendCard.getPostId());
                startActivity(intent);
            }

            @Override
            public void onNewsItemClick(NewsItem newsItem) {
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_id", newsItem.getId());
                startActivity(intent);
            }
        });

        rvHomeUnified.setAdapter(unifiedAdapter);

        rvHomeUnified.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoadingMore && hasMoreData) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            loadMoreData();
                        }
                    }
                }
            }
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
                trendCards.clear();
                trendCards.addAll(cards);
                updateAdapter();
            }

            @Override
            public void onError(String error) {
                Log.e("HomeActivity", "加载趋势卡片失败: " + error);
            }
        });

        homeViewModel.loadNewsList(1, 14, new HomeViewModel.NewsListCallback() {
            @Override
            public void onSuccess(List<NewsItem> news) {
                newsList.clear();
                newsList.addAll(news);
                updateAdapter();
            }

            @Override
            public void onError(String error) {
                Log.e("HomeActivity", "加载新闻列表失败: " + error);
            }
        });
    }

    private void loadMoreData() {
        isLoadingMore = true;
        homeViewModel.incrementPage();

        homeViewModel.loadMoreNews(homeViewModel.getCurrentPage(), new HomeViewModel.NewsListCallback() {
            @Override
            public void onSuccess(List<NewsItem> news) {
                isLoadingMore = false;
                if (news == null || news.isEmpty()) {
                    hasMoreData = false;
                } else {
                    newsList.addAll(news);
                    updateAdapter();
                }
            }

            @Override
            public void onError(String error) {
                isLoadingMore = false;
                Log.e("HomeActivity", "加载更多失败: " + error);
                Toast.makeText(HomeActivity.this, "加载更多失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAdapter() {
        if (unifiedAdapter != null) {
            unifiedAdapter.updateData(trendCards, newsList);
        }
    }

    private void refreshData() {
        homeViewModel.resetPagination();
        hasMoreData = true;
        isLoadingMore = false;

        loadData();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
