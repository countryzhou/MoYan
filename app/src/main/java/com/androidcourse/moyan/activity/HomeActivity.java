package com.androidcourse.moyan.activity;

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

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.TrendCard;
import com.androidcourse.moyan.adapter.NewsAdapter;
import com.androidcourse.moyan.adapter.TrendCardAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // ==================== UI组件声明 ====================
    // 搜索框：用户输入关键词搜索内容
    private EditText etSearch;
    // 头像图标：点击跳转到个人资料页面
    private ImageView ivAvatar;
    // 横向滚动的趋势卡片列表（RecyclerView）
    private RecyclerView rvTrendCards;
    // 纵向滚动的新闻列表（RecyclerView）
    private RecyclerView rvNewsList;
    // 悬浮按钮：点击跳转到发布/编辑页面
    private FloatingActionButton fabWrite;
    // 指示点容器：显示横向滚动卡片的当前位置指示器
    private LinearLayout dotIndicator;

    // ==================== 底部导航栏组件 ====================
    private LinearLayout navHome;      // 首页导航按钮
    private LinearLayout navExplore;   // 探索/互动页面导航按钮
    private LinearLayout navMessages;  // 消息页面导航按钮
    private LinearLayout navProfile;   // 个人资料页面导航按钮

    // ==================== 适配器 ====================
    // 趋势卡片适配器：负责将趋势卡片数据绑定到横向RecyclerView
    private TrendCardAdapter trendCardAdapter;
    // 新闻适配器：负责将新闻数据绑定到纵向RecyclerView
    private NewsAdapter newsAdapter;

    // ==================== 数据源 ====================
    // 趋势卡片数据列表
    private List<TrendCard> trendCardList;
    // 新闻列表数据
    private List<NewsItem> newsList;

    // 主线程Handler：用于在主线程更新UI（本项目暂未使用，但保留以备后续异步操作）
    private Handler mainHandler;
    // 当前登录用户ID，默认为1（实际项目中应从SharedPreferences或登录状态获取）
    private int currentUserId = 1;

    /**
     * Activity创建时调用的生命周期方法
     * 功能：初始化Activity，设置布局，初始化UI组件，设置监听器，加载默认数据和服务器数据
     * 具体实现：
     *   1. 调用setContentView加载activity_home布局
     *   2. 创建主线程Handler
     *   3. 调用initViews()初始化所有UI组件
     *   4. 调用setupListeners()设置所有交互监听器
     *   5. 显示默认的趋势卡片和新闻列表（作为占位数据）
     *   6. 调用loadDataFromServer()从服务器加载真实数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupListeners();

        // 先显示默认数据（保证界面不为空）
        displayTrendCards(TrendCard.getDefaultTrendCards());
        displayNewsList(NewsItem.getDefaultNewsList());

        // 从服务器加载数据（异步替换默认数据）
        loadDataFromServer();
    }

    /**
     * 功能：初始化所有UI组件
     * 具体实现：通过findViewById找到布局文件中定义的各个控件，并赋值给对应的成员变量
     *   包括：搜索框、头像、横向卡片列表、纵向新闻列表、悬浮按钮、指示点容器、底部导航栏四个按钮
     */
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

    /**
     * 功能：设置所有UI组件的交互监听器
     * 具体实现：
     *   1. 搜索框点击监听：跳转到SearchActivity搜索页面
     *   2. 搜索框设置setFocusable(false)：使搜索框不可编辑，点击时整个跳转（常见的设计模式）
     *   3. 头像点击监听：跳转到ProfileActivity个人资料页面
     *   4. 悬浮按钮点击监听：跳转到EditprofileActivity编辑资料页面
     *   5. 底部导航栏四个按钮的点击监听：
     *      - 首页：调用refreshData()刷新数据
     *      - 探索：跳转到InteractionActivity并关闭当前页面
     *      - 消息：跳转到MessageActivity并关闭当前页面
     *      - 个人资料：跳转到ProfileActivity并关闭当前页面
     */
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

    /**
     * 功能：从服务器异步加载数据
     * 具体实现：
     *   1. 调用TrendCard.fetchTrendCards()获取趋势卡片数据
     *      - 成功：调用displayTrendCards()更新UI
     *      - 失败：记录日志，保持原有默认数据
     *   2. 调用NewsItem.fetchNewsList()获取新闻列表数据（第1页，每页20条）
     *      - 成功：调用displayNewsList()更新UI
     *      - 失败：记录日志，保持原有默认数据
     *   注意：两个网络请求是并行执行的，互不影响
     */
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

    /**
     * 功能：在横向RecyclerView中显示趋势卡片列表
     * 具体实现：
     *   1. 保存卡片数据到成员变量trendCardList
     *   2. 设置横向布局管理器（LinearLayoutManager.HORIZONTAL）
     *   3. 创建TrendCardAdapter适配器，设置点击事件（点击卡片跳转到PostdetailActivity帖子详情页）
     *   4. 为RecyclerView设置适配器
     *   5. 通过post()延迟创建指示点（确保RecyclerView已测量完成）
     *   6. 添加滚动监听，当滚动停止时更新指示点位置
     *
     * @param cards 要显示的趋势卡片数据列表
     */
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

    /**
     * 功能：在纵向RecyclerView中显示新闻列表
     * 具体实现：
     *   1. 保存新闻数据到成员变量newsList
     *   2. 设置纵向布局管理器（默认VERTICAL）
     *   3. 创建NewsAdapter适配器，设置点击事件（点击新闻项跳转到PostdetailActivity详情页，并传递帖子ID、标题、作者、评论数）
     *   4. 为RecyclerView设置适配器
     *
     * @param news 要显示的新闻列表数据
     */
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

    /**
     * 功能：创建横向卡片列表的指示点（圆点指示器）
     * 具体实现：
     *   1. 如果卡片数量<=1，隐藏指示点容器（不需要指示）
     *   2. 否则清空容器中所有现有子View
     *   3. 循环创建count个圆点View，每个圆点宽高8dp
     *   4. 为每个圆点设置未激活背景（bg_dot_inactive）
     *   5. 将所有圆点添加到dotIndicator容器中
     *   6. 显示指示点容器，并默认选中第一个圆点（位置0）
     *
     * @param count 卡片数量，即需要创建的圆点数量
     */
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

    /**
     * 功能：选中指定位置的指示点（高亮显示）
     * 具体实现：
     *   1. 检查dotIndicator是否存在且含有子View
     *   2. 遍历所有圆点，将选中位置的圆点背景设为激活状态（bg_dot_active）
     *   3. 其他圆点背景设为未激活状态（bg_dot_inactive）
     *
     * @param position 要选中的圆点位置索引（从0开始）
     */
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

    /**
     * 功能：根据横向RecyclerView的滚动位置更新指示点
     * 具体实现：
     *   1. 获取RecyclerView的布局管理器
     *   2. 获取当前第一个完全可见的卡片位置
     *   3. 如果位置有效，调用selectDot()选中该位置对应的圆点
     *   注意：此方法通常在滚动停止时调用
     */
    private void updateDotByScroll() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rvTrendCards.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        if (firstVisiblePosition != RecyclerView.NO_POSITION) {
            selectDot(firstVisiblePosition);
        }
    }

    /**
     * 功能：将dp单位转换为像素px单位
     * 具体实现：根据设备屏幕密度(density)，计算 dp值 * density = 实际像素值
     * 用途：在代码中动态设置View的宽高、边距时，使用dp保证不同屏幕密度下的显示效果一致
     *
     * @param dp 要转换的dp值
     * @return 转换后的px像素值
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * 功能：手动刷新首页数据
     * 具体实现：
     *   1. 显示一个短暂的Toast提示"刷新中..."
     *   2. 调用loadDataFromServer()重新从服务器加载最新数据
     *   注意：此方法由底部导航栏"首页"按钮点击时触发
     */
    private void refreshData() {
        Toast.makeText(this, "刷新中...", Toast.LENGTH_SHORT).show();
        loadDataFromServer();
    }
}