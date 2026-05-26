package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.adapter.PostAdapter;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户发帖记录页面
 * 显示当前用户发布的所有帖子
 */
public class UserPostsActivity extends AppCompatActivity implements PostAdapter.OnPostClickListener {

    private RecyclerView rvPosts;
    private LinearLayout llEmpty, llLoading;
    private ImageView ivBack;

    private ProfileViewModel profileViewModel;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);

        // 检查登录状态
        if (!SharedPrefsHelper.getInstance().isLogin()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadUserPosts();
    }

    private void initViews() {
        rvPosts = findViewById(R.id.rv_posts);
        llEmpty = findViewById(R.id.ll_empty);
        llLoading = findViewById(R.id.ll_loading);
        ivBack = findViewById(R.id.iv_back);

        profileViewModel = new ProfileViewModel();

        ivBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList, this);

        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postAdapter);

        // 添加滚动监听，实现加载更多
        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && !isLoading && hasMoreData) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    // 当滑动到倒数第5个item时加载更多
                    if (lastVisibleItem >= totalItemCount - 5) {
                        loadMorePosts();
                    }
                }
            }
        });
    }

    private void loadUserPosts() {
        if (isLoading) return;

        isLoading = true;
        llLoading.setVisibility(View.VISIBLE);

        int userId = SharedPrefsHelper.getInstance().getUserId();

        profileViewModel.loadUserPosts(userId, currentPage, PAGE_SIZE, new ProfileViewModel.UserPostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                isLoading = false;
                llLoading.setVisibility(View.GONE);

                if (posts == null || posts.isEmpty()) {
                    hasMoreData = false;
                    if (postList.isEmpty()) {
                        showEmptyState();
                    }
                    return;
                }

                postList.addAll(posts);
                postAdapter.updateData(postList);

                // 判断是否还有更多数据
                if (posts.size() < PAGE_SIZE) {
                    hasMoreData = false;
                }

                currentPage++;
            }

            @Override
            public void onFailure(String error) {
                isLoading = false;
                llLoading.setVisibility(View.GONE);
                Toast.makeText(UserPostsActivity.this, "加载失败：" + error, Toast.LENGTH_SHORT).show();

                if (postList.isEmpty()) {
                    showEmptyState();
                }
            }
        });
    }

    private void loadMorePosts() {
        if (isLoading || !hasMoreData) return;

        isLoading = true;
        llLoading.setVisibility(View.VISIBLE);

        int userId = SharedPrefsHelper.getInstance().getUserId();

        profileViewModel.loadUserPosts(userId, currentPage, PAGE_SIZE, new ProfileViewModel.UserPostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                isLoading = false;
                llLoading.setVisibility(View.GONE);

                if (posts == null || posts.isEmpty()) {
                    hasMoreData = false;
                    return;
                }

                postList.addAll(posts);
                postAdapter.notifyItemRangeInserted(postList.size() - posts.size(), posts.size());

                // 判断是否还有更多数据
                if (posts.size() < PAGE_SIZE) {
                    hasMoreData = false;
                }

                currentPage++;
            }

            @Override
            public void onFailure(String error) {
                isLoading = false;
                llLoading.setVisibility(View.GONE);
                Toast.makeText(UserPostsActivity.this, "加载更多失败：" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        rvPosts.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostClick(Post post) {
        // 跳转到帖子详情页
        Intent intent = new Intent(this, PostdetailActivity.class);
        intent.putExtra("postId", post.getPostId());
        startActivity(intent);
    }

    @Override
    public void onAvatarClick(Post post) {
        // 点击头像，如果是自己的帖子可以跳转到编辑资料或其他页面
        // 这里暂时不做处理
    }
}
