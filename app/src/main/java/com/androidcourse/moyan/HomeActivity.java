package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.adapter.NewsAdapter;
import com.androidcourse.moyan.adapter.TrendCardAdapter;
import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.TrendCard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // UI组件
    private EditText etSearch;
    private ImageView ivAvatar;
    private RecyclerView rvTrendCards;
    private RecyclerView rvNewsList;  // 新闻列表 RecyclerView
    private FloatingActionButton fabWrite;

    // 底部导航栏
    private LinearLayout navHome, navExplore, navMessages, navProfile;

    // 适配器
    private TrendCardAdapter trendCardAdapter;
    private NewsAdapter newsAdapter;

    // 数据源
    private List<TrendCard> trendCardList;
    private List<NewsItem> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupListeners();
        loadTrendCards();
        loadNewsList();
    }

    private void initViews() {
        // 注意：需要先在 activity_home.xml 中为这些组件添加 id
        etSearch = findViewById(R.id.et_search);
        ivAvatar = findViewById(R.id.iv_avatar);
        rvTrendCards = findViewById(R.id.rv_trend_cards);
        rvNewsList = findViewById(R.id.rv_news_list);  // 使用 RecyclerView
        fabWrite = findViewById(R.id.fab_write);

        // 底部导航栏
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupListeners() {
        // 搜索框点击
        if (etSearch != null) {
            etSearch.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            });
            etSearch.setFocusable(false);
            etSearch.setCursorVisible(false);
        }

        // 头像点击
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // 写帖子按钮
        fabWrite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EditprofileActivity.class);
            startActivity(intent);
        });

        // 底部导航栏
        navHome.setOnClickListener(v -> {
            // 已经在首页，可以刷新数据
            refreshData();
        });
        navExplore.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, InteractionActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
        navMessages.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MessageActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void loadTrendCards() {
        trendCardList = new ArrayList<>();
        // 添加示例数据
        trendCardList.add(new TrendCard("热门推荐", "今日热点新闻", R.drawable.img_car_placeholder));
        trendCardList.add(new TrendCard("科技前沿", "最新科技动态", R.drawable.img_car_placeholder));
        trendCardList.add(new TrendCard("娱乐八卦", "明星最新资讯", R.drawable.img_car_placeholder));
        trendCardList.add(new TrendCard("体育赛事", "精彩比赛回顾", R.drawable.img_car_placeholder));

        if (rvTrendCards != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rvTrendCards.setLayoutManager(layoutManager);
            trendCardAdapter = new TrendCardAdapter(this, trendCardList);

            // 修改这里：添加点击事件监听器
            trendCardAdapter = new TrendCardAdapter(this, trendCardList, trendCard -> {
                // 点击趋势卡片，跳转到详情页
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_id", trendCard.getTitle());
                intent.putExtra("news_title", trendCard.getTitle());
                intent.putExtra("news_author", trendCard.getAuthor());
                intent.putExtra("source", "trend"); // 标记来源
                startActivity(intent);
            });

            rvTrendCards.setAdapter(trendCardAdapter);
        }
    }

    private void loadNewsList() {
        newsList = new ArrayList<>();

        // 添加示例新闻数据（模拟从服务器获取）
        for (int i = 1; i <= 10; i++) {
            NewsItem news = new NewsItem(
                    i,
                    "精彩新闻标题 " + i,
                    "这是新闻的详细描述内容，会显示在卡片上吸引用户点击阅读更多精彩内容。新闻描述通常会包含一些关键信息来引起读者的兴趣。",
                    "作者 " + i,
                    System.currentTimeMillis() - (i * 3600000L),
                    R.drawable.img_car_placeholder
            );
            // 设置随机点赞和评论数
            //news.setLikeCount((int)(Math.random() * 1000));
            news.setCommentCount((int)(Math.random() * 100));
            newsList.add(news);
        }

        // 设置 RecyclerView
        if (rvNewsList != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rvNewsList.setLayoutManager(layoutManager);

            // 创建适配器并设置点击事件
            newsAdapter = new NewsAdapter(this, newsList, newsItem -> {
                // 点击新闻条目，跳转到帖子详情页
                Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
                intent.putExtra("post_id", newsItem.getId());
                intent.putExtra("news_title", newsItem.getTitle());
                //intent.putExtra("news_summary", newsItem.getSummary());
                intent.putExtra("news_author", newsItem.getAuthor());
                //intent.putExtra("news_like_count", newsItem.getLikeCount());
                intent.putExtra("news_comment_count", newsItem.getCommentCount());
                startActivity(intent);
            });

            rvNewsList.setAdapter(newsAdapter);
        }
    }

    // 刷新数据的方法
    private void refreshData() {
        // 可以在这里重新从服务器获取最新数据
        Toast.makeText(this, "刷新中...", Toast.LENGTH_SHORT).show();
        loadNewsList();  // 重新加载数据
        if (newsAdapter != null) {
            newsAdapter.updateData(newsList);
        }
        Toast.makeText(this, "刷新完成", Toast.LENGTH_SHORT).show();
    }
}