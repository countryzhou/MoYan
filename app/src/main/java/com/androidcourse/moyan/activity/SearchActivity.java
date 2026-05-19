package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.androidcourse.moyan.R;
import com.androidcourse.moyan.adapter.PostAdapter;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.repository.PostRepository;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    // 搜索栏控件
    private EditText etSearch;
    private ImageView ivSearch;
    private TextView tvBack;

    // 历史记录视图
    private LinearLayout viewHistory;
    private RecyclerView rvHistory;
    private ImageView ivClearAll;
    private List<String> historyList = new ArrayList<>();
    private HistoryAdapter historyAdapter;

    // 搜索结果视图
    private View viewResult;
    private RecyclerView rvSearchResult;
    private TextView tvAll, tvUser, tvImage;
    private TextView tvComprehensive, tvLatest;

    // 数据
    private PostAdapter postAdapter;
    private List<Post> searchResults = new ArrayList<>();
    private String currentKeyword = "";
    private String currentCategory = "all";
    private String currentSort = "comprehensive";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        loadHistory();
        setupListeners();
        setupCategoryListeners();
        setupSortListeners();

        // 使用 OnBackPressedDispatcher 处理返回事件
        setupBackPressedCallback();
    }

    private void initViews() {
        // 搜索栏
        tvBack = findViewById(R.id.tv_back);
        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.tv_search);

        // 历史记录视图
        viewHistory = findViewById(R.id.view_history);
        rvHistory = findViewById(R.id.rv_history);
        ivClearAll = findViewById(R.id.iv_clear_all);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        // 搜索结果视图
        viewResult = findViewById(R.id.view_result);
        rvSearchResult = viewResult.findViewById(R.id.rv_search_result);
        tvAll = viewResult.findViewById(R.id.tv_all);
        tvUser = viewResult.findViewById(R.id.tv_user);
        tvImage = viewResult.findViewById(R.id.tv_image);
        tvComprehensive = viewResult.findViewById(R.id.tv_comprehensive);
        tvLatest = viewResult.findViewById(R.id.tv_latest);

        // 设置RecyclerView
        if (rvSearchResult != null) {
            rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setupListeners() {
        // 返回按钮
        tvBack.setOnClickListener(v -> finish());

        // 搜索按钮
        ivSearch.setOnClickListener(v -> performSearch());

        // 搜索框输入监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 可以添加实时搜索功能
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 清空历史记录
        ivClearAll.setOnClickListener(v -> clearHistory());
    }

    private void setupCategoryListeners() {
        tvAll.setOnClickListener(v -> switchCategory("all"));
        tvUser.setOnClickListener(v -> switchCategory("user"));
        tvImage.setOnClickListener(v -> switchCategory("image"));
    }

    private void setupSortListeners() {
        tvComprehensive.setOnClickListener(v -> switchSort("comprehensive"));
        tvLatest.setOnClickListener(v -> switchSort("latest"));
    }

    private void switchCategory(String category) {
        currentCategory = category;
        updateCategoryUI();
        performSearch();
    }

    private void switchSort(String sort) {
        currentSort = sort;
        updateSortUI();
        performSearch();
    }

    private void updateCategoryUI() {
        // 使用资源颜色
        int defaultColor = getColor(R.color.text_main);
        int accentColor = getColor(R.color.colorAccent);
        
        tvAll.setTextColor(defaultColor);
        tvUser.setTextColor(defaultColor);
        tvImage.setTextColor(defaultColor);
        
        switch (currentCategory) {
            case "all":
                tvAll.setTextColor(accentColor);
                break;
            case "user":
                tvUser.setTextColor(accentColor);
                break;
            case "image":
                tvImage.setTextColor(accentColor);
                break;
        }
    }

    private void updateSortUI() {
        int defaultColor = getColor(R.color.text_secondary);
        int accentColor = getColor(R.color.colorAccent);
        
        tvComprehensive.setTextColor(defaultColor);
        tvLatest.setTextColor(defaultColor);
        
        if ("comprehensive".equals(currentSort)) {
            tvComprehensive.setTextColor(accentColor);
        } else {
            tvLatest.setTextColor(accentColor);
        }
    }

    private void performSearch() {
        String keyword = etSearch.getText().toString().trim();
        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }

        currentKeyword = keyword;

        // 保存到历史记录
        saveToHistory(keyword);

        // 切换视图：隐藏历史记录，显示搜索结果
        viewHistory.setVisibility(View.GONE);
        viewResult.setVisibility(View.VISIBLE);

        // 执行搜索
        executeSearch();
    }

    private void executeSearch() {
        PostRepository repository = new PostRepository();
        String sortBy = "comprehensive".equals(currentSort) ? "hot" : "time";

        switch (currentCategory) {
            case "all":
                searchPosts(repository, currentKeyword, sortBy);
                break;
            case "user":
                searchUsers(currentKeyword);
                break;
            case "image":
                searchPostsWithImages(repository, currentKeyword, sortBy);
                break;
        }
    }

    private void searchPosts(PostRepository repository, String keyword, String sortBy) {
        repository.searchPosts(keyword, "", sortBy, 1,
                new PostRepository.RepositoryCallback<List<Post>>() {
                    @Override
                    public void onResult(List<Post> result) {
                        runOnUiThread(() -> {
                            searchResults = result;
                            postAdapter = new PostAdapter(SearchActivity.this, result,
                                    new PostAdapter.OnPostClickListener() {
                                        @Override
                                        public void onPostClick(Post post) {
                                            openPostDetail(post);
                                        }

                                        @Override
                                        public void onAvatarClick(Post post) {
                                            openUserProfile(post);
                                        }
                                    });
                            rvSearchResult.setAdapter(postAdapter);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
    }

    private void searchUsers(String keyword) {
        // TODO: 实现用户搜索功能
        Toast.makeText(this, "用户搜索功能开发中", Toast.LENGTH_SHORT).show();

        // 临时显示提示
        searchResults.clear();
        if (postAdapter != null) {
            postAdapter.notifyDataSetChanged();
        }
    }

    private void searchPostsWithImages(PostRepository repository, String keyword, String sortBy) {
        // 搜索带图片的帖子
        repository.searchPosts(keyword, "image", sortBy, 1,
                new PostRepository.RepositoryCallback<List<Post>>() {
                    @Override
                    public void onResult(List<Post> result) {
                        runOnUiThread(() -> {
                            // 过滤出有图片的帖子
                            List<Post> imagePosts = new ArrayList<>();
                            for (Post post : result) {
                                if (post.getImagePaths() != null && !post.getImagePaths().isEmpty()) {
                                    imagePosts.add(post);
                                }
                            }

                            searchResults = imagePosts;
                            postAdapter = new PostAdapter(SearchActivity.this, imagePosts,
                                    new PostAdapter.OnPostClickListener() {
                                        @Override
                                        public void onPostClick(Post post) {
                                            openPostDetail(post);
                                        }

                                        @Override
                                        public void onAvatarClick(Post post) {
                                            openUserProfile(post);
                                        }
                                    });
                            rvSearchResult.setAdapter(postAdapter);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
    }

    private void openPostDetail(Post post) {
        Intent intent = new Intent(this, PostdetailActivity.class);
        intent.putExtra("post_id", post.getPostId());
        startActivity(intent);
    }

    private void openUserProfile(Post post) {
        if (post.isProfileAccessible()) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra("user_id", post.getUserId());
            startActivity(intent);
        }
    }

    // ==================== 搜索历史管理 ====================

    private void loadHistory() {
        SharedPrefsHelper sp = SharedPrefsHelper.getInstance();
        String historyJson = sp.getString("search_history", "");
        if (!historyJson.isEmpty()) {
            try {
                Gson gson = new Gson();
                String[] history = gson.fromJson(historyJson, String[].class);
                historyList.clear();
                for (String item : history) {
                    historyList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        historyAdapter = new HistoryAdapter(historyList);
        rvHistory.setAdapter(historyAdapter);
    }

    private void saveToHistory(String keyword) {
        if (historyList.contains(keyword)) {
            historyList.remove(keyword);
        }
        historyList.add(0, keyword);

        // 最多保存20条
        if (historyList.size() > 20) {
            historyList = historyList.subList(0, 20);
        }

        Gson gson = new Gson();
        SharedPrefsHelper.getInstance().putString("search_history", gson.toJson(historyList));

        if (historyAdapter != null) {
            historyAdapter.notifyDataSetChanged();
        }
    }

    private void clearHistory() {
        historyList.clear();
        SharedPrefsHelper.getInstance().putString("search_history", "");
        if (historyAdapter != null) {
            historyAdapter.notifyDataSetChanged();
        }
        Toast.makeText(this, "历史记录已清空", Toast.LENGTH_SHORT).show();
    }

    /**
     * 使用 OnBackPressedDispatcher 处理返回事件（兼容 Android 14+）
     */
    private void setupBackPressedCallback() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 如果当前显示搜索结果，按返回键回到历史记录视图
                if (viewResult.getVisibility() == View.VISIBLE) {
                    viewResult.setVisibility(View.GONE);
                    viewHistory.setVisibility(View.VISIBLE);
                } else {
                    // 如果没有搜索结果视图显示，调用 finish
                    finish();
                }
            }
        });
    }

    // ==================== 历史记录适配器 ====================

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<String> historyList;

        public HistoryAdapter(List<String> historyList) {
            this.historyList = historyList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String keyword = historyList.get(position);
            holder.textView.setText(keyword);
            holder.itemView.setOnClickListener(v -> {
                etSearch.setText(keyword);
                performSearch();
            });
        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}