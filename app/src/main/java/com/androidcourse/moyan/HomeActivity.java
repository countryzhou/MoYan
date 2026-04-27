package com.androidcourse.moyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // UI组件
    private EditText etSearch;
    private ImageView ivAvatar;
    private RecyclerView rvTrendCards;
    private RecyclerView rvNewsList;
    private FloatingActionButton fabWrite;

    // 底部导航栏
    private LinearLayout navHome, navExplore, navMessages, navProfile;

    // 适配器
    private TrendCardAdapter trendCardAdapter;
    private NewsAdapter newsAdapter;

    // 数据源
    private List<TrendCard> trendCardList;
    private List<NewsItem> newsList;

    // 用于在主线程更新UI
    private Handler mainHandler;

    // 当前用户ID（暂时写死，登录后从SharedPreferences获取）
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupListeners();

        // 从服务器加载数据
        loadTrendCardsFromServer();  // 加载趋势卡片
        loadNewsListFromServer();    // 加载新闻列表
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        ivAvatar = findViewById(R.id.iv_avatar);
        rvTrendCards = findViewById(R.id.rv_trend_cards);
        rvNewsList = findViewById(R.id.rv_news_list);
        fabWrite = findViewById(R.id.fab_write);

        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navMessages = findViewById(R.id.nav_messages);
        navProfile = findViewById(R.id.nav_profile);
    }

    private void setupListeners() {
        // 搜索框
        etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        etSearch.setFocusable(false);

        // 头像
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // 写帖子
        fabWrite.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EditprofileActivity.class);
            startActivity(intent);
        });

        // 底部导航
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
     * 从服务器加载趋势卡片
     * 接口：getPostList 获取热门帖子，前4个作为趋势卡片
     */
    private void loadTrendCardsFromServer() {
        new Thread(() -> {
            try {
                // 构建请求JSON
                JSONObject request = new JSONObject();
                request.put("action", "getPostList");

                JSONObject params = new JSONObject();
                params.put("page", 1);
                params.put("size", 4);  // 只要前4条
                params.put("userId", currentUserId);
                request.put("params", params);

                // 发送请求
                String response = sendSocketRequest(request.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("code") == 0) {
                    // 解析帖子数据
                    JSONArray posts = jsonResponse.getJSONArray("data");
                    List<TrendCard> cards = new ArrayList<>();

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        TrendCard card = new TrendCard(
                                post.getString("title"),
                                post.getString("content"),
                                R.drawable.img_car_placeholder
                        );
                        // 保存帖子ID，用于点击跳转
                        card.setPostId(post.getInt("postId"));
                        cards.add(card);
                    }

                    // 如果服务器没有数据，使用默认数据
                    if (cards.isEmpty()) {
                        cards = getDefaultTrendCards();
                    }

                    final List<TrendCard> finalCards = cards;
                    mainHandler.post(() -> displayTrendCards(finalCards));
                } else {
                    // 请求失败，使用默认数据
                    mainHandler.post(() -> displayTrendCards(getDefaultTrendCards()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 出错时使用默认数据
                mainHandler.post(() -> displayTrendCards(getDefaultTrendCards()));
            }
        }).start();
    }

    /**
     * 从服务器加载新闻列表
     * 接口：getPostList 获取帖子列表（分页）
     */
    private void loadNewsListFromServer() {
        new Thread(() -> {
            try {
                // 构建请求JSON
                JSONObject request = new JSONObject();
                request.put("action", "getPostList");

                JSONObject params = new JSONObject();
                params.put("page", 1);
                params.put("size", 20);  // 每页20条
                params.put("userId", currentUserId);
                request.put("params", params);

                // 发送请求
                String response = sendSocketRequest(request.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("code") == 0) {
                    // 解析帖子数据
                    JSONArray posts = jsonResponse.getJSONArray("data");
                    List<NewsItem> news = new ArrayList<>();

                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        NewsItem item = new NewsItem(
                                post.getInt("postId"),
                                post.getString("title"),
                                post.getString("content"),
                                post.getString("nickname"),  // 作者昵称
                                post.getLong("createTime"),
                                R.drawable.img_car_placeholder
                        );
                        item.setCommentCount(post.getInt("replyCount"));
                        news.add(item);
                    }

                    final List<NewsItem> finalNews = news;
                    mainHandler.post(() -> displayNewsList(finalNews));
                } else {
                    // 请求失败，使用默认数据
                    mainHandler.post(() -> displayNewsList(getDefaultNewsList()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 出错时使用默认数据
                mainHandler.post(() -> displayNewsList(getDefaultNewsList()));
            }
        }).start();
    }

    /**
     * 显示趋势卡片
     */
    private void displayTrendCards(List<TrendCard> cards) {
        trendCardList = cards;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTrendCards.setLayoutManager(layoutManager);

        trendCardAdapter = new TrendCardAdapter(this, trendCardList, trendCard -> {
            // 点击趋势卡片，跳转到帖子详情
            Intent intent = new Intent(HomeActivity.this, PostdetailActivity.class);
            intent.putExtra("post_id", trendCard.getPostId());
            intent.putExtra("news_title", trendCard.getTitle());
            startActivity(intent);
        });

        rvTrendCards.setAdapter(trendCardAdapter);
    }

    /**
     * 显示新闻列表
     */
    private void displayNewsList(List<NewsItem> news) {
        newsList = news;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvNewsList.setLayoutManager(layoutManager);

        newsAdapter = new NewsAdapter(this, newsList, newsItem -> {
            // 点击新闻，跳转到帖子详情
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
     * 发送Socket请求到服务器
     * @param jsonData JSON格式的请求数据
     * @return 服务器响应
     */
    private String sendSocketRequest(String jsonData) throws Exception {
        // 服务器地址（请修改为你的服务器IP）
        String serverIp = "192.168.1.100";  // TODO: 修改为你的服务器IP
        int serverPort = 8888;

        Socket socket = new Socket(serverIp, serverPort);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // 发送数据（注意：需要加两个换行符）
        out.print(jsonData + "\n\n");
        out.flush();

        // 读取响应
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        socket.close();
        return response.toString();
    }

    /**
     * 获取默认的趋势卡片（服务器没数据时使用）
     */
    private List<TrendCard> getDefaultTrendCards() {
        List<TrendCard> cards = new ArrayList<>();
        cards.add(new TrendCard("热门推荐", "今日热点新闻", R.drawable.img_car_placeholder));
        cards.add(new TrendCard("科技前沿", "最新科技动态", R.drawable.img_car_placeholder));
        cards.add(new TrendCard("娱乐八卦", "明星最新资讯", R.drawable.img_car_placeholder));
        cards.add(new TrendCard("体育赛事", "精彩比赛回顾", R.drawable.img_car_placeholder));
        return cards;
    }

    /**
     * 获取默认的新闻列表（服务器没数据时使用）
     */
    private List<NewsItem> getDefaultNewsList() {
        List<NewsItem> news = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            NewsItem item = new NewsItem(
                    i,
                    "示例新闻标题 " + i,
                    "这是示例新闻内容，当服务器没有数据时会显示这些内容。请确保服务器正常运行。",
                    "系统作者",
                    System.currentTimeMillis(),
                    R.drawable.img_car_placeholder
            );
            item.setCommentCount((int)(Math.random() * 100));
            news.add(item);
        }
        return news;
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        Toast.makeText(this, "刷新中...", Toast.LENGTH_SHORT).show();
        loadTrendCardsFromServer();
        loadNewsListFromServer();
    }
}