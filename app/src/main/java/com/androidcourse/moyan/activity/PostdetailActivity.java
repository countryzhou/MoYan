package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.adapter.ReplyAdapter;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.model.Reply;
import com.androidcourse.moyan.utils.TimeUtils;
import com.androidcourse.moyan.viewmodel.PostDetailViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 帖子详情页面
 */
public class PostdetailActivity extends AppCompatActivity {

    // 顶部栏
    private ImageView btnBack;
    private CircleImageView ivAuthorAvatar;
    private TextView tvAuthorName;
    private androidx.appcompat.widget.AppCompatButton btnFollow;

    // 内容区
    private TextView tvPostTitle;
    private TextView tvPostContent;
    private LinearLayout llImageContainer;
    private TextView tvEditInfo;
    private LinearLayout layoutTags;
    private TextView tvTag1, tvTag2, tvTag3, tvTag4;

    // 互动统计区
    private TextView tvCommentCount;

    // 互动区
    private ImageView ivCommentBottom;
    private TextView tvCommentCountBottom;
    private ImageView ivCollect;
    private TextView tvCollectCountBottom;
    private LinearLayout layoutShare;
    private LinearLayout layoutReport;
    private ImageView ivLikeBottom;
    private TextView tvLikeCountBottom;

    // 回复区
    private RecyclerView rvReplies;
    private TextView etComment;
    private TextView tvLoadMoreHint;
    private SwipeRefreshLayout swipeRefresh;
    private NestedScrollView scrollView;

    private Post currentPost;
    private ReplyAdapter replyAdapter;
    private final List<Reply> replyList = new ArrayList<>();
    private PostDetailViewModel viewModel;

    private boolean isCollected = false;
    private boolean isLoading = false;      // 是否正在加载
    private boolean hasMore = true;         // 是否还有更多数据
    private int currentPage = 1;            // 当前页码
    private static final int PAGE_SIZE = 10; // 每页大小

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        viewModel = new PostDetailViewModel();
        initViews();
        setupListeners();
        setupScrollListener();
        loadPostData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivAuthorAvatar = findViewById(R.id.ivAuthorAvatar);
        tvAuthorName = findViewById(R.id.tvAuthorName);
        btnFollow = findViewById(R.id.btnFollow);

        tvPostTitle = findViewById(R.id.tvPostTitle);
        tvPostContent = findViewById(R.id.tvPostContent);
        llImageContainer = findViewById(R.id.llImageContainer);
        tvEditInfo = findViewById(R.id.tvEditInfo);
        layoutTags = findViewById(R.id.layoutTags);
        tvTag1 = findViewById(R.id.tvTag1);
        tvTag2 = findViewById(R.id.tvTag2);
        tvTag3 = findViewById(R.id.tvTag3);
        tvTag4 = findViewById(R.id.tvTag4);

        tvCommentCount = findViewById(R.id.tvCommentCount);
        ivCommentBottom = findViewById(R.id.ivCommentBottom);
        tvCommentCountBottom = findViewById(R.id.tvCommentCountBottom);
        ivCollect = findViewById(R.id.ivCollect);
        tvCollectCountBottom = findViewById(R.id.ivCollectCountBottom);
        layoutShare = findViewById(R.id.layoutShare);
        layoutReport = findViewById(R.id.layoutReport);
        ivLikeBottom = findViewById(R.id.ivLikeBottom);
        tvLikeCountBottom = findViewById(R.id.tvLikeCountBottom);

        rvReplies = findViewById(R.id.rvComments);
        rvReplies.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new ReplyAdapter(replyList, getCurrentUserId());
        rvReplies.setAdapter(replyAdapter);
        etComment = findViewById(R.id.etComment);
        tvLoadMoreHint = findViewById(R.id.tvLoadMoreHint);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        scrollView = findViewById(R.id.scrollView);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        layoutShare.setOnClickListener(v -> sharePost());
        btnFollow.setOnClickListener(v -> toggleFollow());
        ivCollect.setOnClickListener(v -> toggleCollect());
        ivCommentBottom.setOnClickListener(v -> scrollToReplies());

        // 下拉刷新
        swipeRefresh.setOnRefreshListener(() -> {
            refreshReplies();
        });

        // 举报按钮
        layoutReport.setOnClickListener(v -> {
            if (currentPost == null) {
                Toast.makeText(this, "帖子信息异常", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(PostdetailActivity.this, ReportActivity.class);
            intent.putExtra("target_type", 1);
            intent.putExtra("target_id", currentPost.getPostId());
            startActivity(intent);
        });

        etComment.setOnClickListener(v -> {
            Intent intent = new Intent(PostdetailActivity.this, CreateReplyActivity.class);
            intent.putExtra("post_id", currentPost != null ? currentPost.getPostId() : -1);
            startActivity(intent);
        });

        replyAdapter.setOnReplyActionListener(new ReplyAdapter.OnReplyActionListener() {
            @Override
            public void onReplyClick(Reply reply) {
                Intent intent = new Intent(PostdetailActivity.this, CreateReplyActivity.class);
                intent.putExtra("post_id", currentPost != null ? currentPost.getPostId() : -1);
                intent.putExtra("parent_reply_id", reply.getReplyId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Reply reply, int position) {
                deleteReply(reply, position);
            }

            @Override
            public void onAvatarClick(Reply reply) {
                if (reply.isProfileAccessible()) {
                    openUserProfile(reply.getUserId());
                }
            }
        });
    }

    // 设置滚动监听，实现上拉加载更多
    private void setupScrollListener() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // 判断是否滚动到底部
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            int diff = view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());

            if (diff <= 0 && !isLoading && hasMore) {
                loadMoreReplies();
            }
        });
    }

    private void loadPostData() {
        int postId = getIntent().getIntExtra("post_id", 1);
        viewModel.loadPostDetail(postId, new PostDetailViewModel.PostDetailCallback() {
            @Override
            public void onSuccess(Post post) {
                currentPost = post;
                displayPostData();
                // 加载第一页回复
                currentPage = 1;
                hasMore = true;
                loadReplies(currentPage);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(PostdetailActivity.this, error, Toast.LENGTH_SHORT).show();
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void displayPostData() {
        if (currentPost == null) return;

        if (currentPost.isAnonymous()) {
            tvAuthorName.setText("匿名用户");
            ivAuthorAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            ivAuthorAvatar.setClickable(false);
            btnFollow.setVisibility(View.GONE);
        } else {
            tvAuthorName.setText(currentPost.getDisplayName());
            if (!TextUtils.isEmpty(currentPost.getAvatarUrl())) {
                Glide.with(this)
                        .load(currentPost.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .into(ivAuthorAvatar);
            }
            ivAuthorAvatar.setClickable(true);
            ivAuthorAvatar.setOnClickListener(v -> openUserProfile(currentPost.getUserId()));
        }

        tvPostTitle.setText(currentPost.getTitle());
        tvPostContent.setText(currentPost.getContent());

        tvCollectCountBottom.setText(String.valueOf(currentPost.getCollectCount()));
        //tvLikeCountBottom.setText(String.valueOf(currentPost.getLikeCount()));

        tvEditInfo.setText("编辑于 " + TimeUtils.formatDateTime(currentPost.getUpdateTime()));
        displayTags(currentPost.getTags());

        displayPostImages(currentPost);
    }

    private void displayPostImages(Post post) {
        if (llImageContainer == null) return;
        llImageContainer.removeAllViews();

        if (post != null && post.hasImages()) {
            llImageContainer.setVisibility(View.VISIBLE);
            for (String imagePath : post.getImagePaths()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 8);
                imageView.setLayoutParams(params);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Glide.with(this).load(imagePath).placeholder(R.drawable.img_car_placeholder).into(imageView);
                llImageContainer.addView(imageView);
            }
        } else {
            llImageContainer.setVisibility(View.GONE);
        }
    }

    private void displayTags(String tags) {
        if (TextUtils.isEmpty(tags)) {
            layoutTags.setVisibility(View.GONE);
            return;
        }
        String[] tagArray = tags.split(",");
        TextView[] tagViews = {tvTag1, tvTag2, tvTag3, tvTag4};
        layoutTags.setVisibility(View.VISIBLE);
        for (int i = 0; i < tagViews.length; i++) {
            if (i < tagArray.length && !TextUtils.isEmpty(tagArray[i].trim())) {
                tagViews[i].setVisibility(View.VISIBLE);
                tagViews[i].setText("#" + tagArray[i].trim());
            } else {
                tagViews[i].setVisibility(View.GONE);
            }
        }
    }

    // 加载回复列表（分页）
    private void loadReplies(int page) {
        if (isLoading) return;
        isLoading = true;

        int postId = getIntent().getIntExtra("post_id", 1);
        viewModel.loadReplies(postId, page, new PostDetailViewModel.ReplyListCallback() {
            @Override
            public void onSuccess(List<Reply> replies) {
                isLoading = false;
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }

                // 判断是否还有更多数据
                if (replies == null || replies.isEmpty()) {
                    hasMore = false;
                    tvLoadMoreHint.setText("没有更多评论了");
                    tvLoadMoreHint.setVisibility(View.VISIBLE);
                } else if (replies.size() < PAGE_SIZE) {
                    hasMore = false;
                    tvLoadMoreHint.setText("已加载全部评论");
                    tvLoadMoreHint.setVisibility(View.VISIBLE);
                } else {
                    hasMore = true;
                    tvLoadMoreHint.setText("上滑加载更多评论");
                    tvLoadMoreHint.setVisibility(View.VISIBLE);
                }

                if (page == 1) {
                    // 第一页，清空列表
                    replyList.clear();
                }

                replyList.addAll(replies);
                replyAdapter.updateReplies(replyList);
                updateReplyCount(replyList.size());

                if (currentPost != null) {
                    currentPost.setReplyCount(replyList.size());
                }
            }

            @Override
            public void onFailure(String error) {
                isLoading = false;
                if (swipeRefresh.isRefreshing()) {
                    swipeRefresh.setRefreshing(false);
                }
                Toast.makeText(PostdetailActivity.this, "加载评论失败: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 加载更多回复
    private void loadMoreReplies() {
        if (!hasMore) {
            return;
        }
        currentPage++;
        loadReplies(currentPage);
    }

    // 刷新回复列表
    private void refreshReplies() {
        currentPage = 1;
        hasMore = true;
        tvLoadMoreHint.setText("加载中...");
        loadReplies(currentPage);
    }

    private void updateReplyCount(int count) {
        tvCommentCount.setText("共" + count + "条评论");
        tvCommentCountBottom.setText(String.valueOf(count));
        if (currentPost != null) {
            currentPost.setReplyCount(count);
        }
    }

    private void deleteReply(Reply reply, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条回复吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    replyList.remove(position);
                    replyAdapter.notifyItemRemoved(position);
                    updateReplyCount(replyList.size());
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void toggleCollect() {
        if (currentPost == null) return;
        isCollected = !isCollected;
        if (isCollected) {
            ivCollect.setImageResource(R.drawable.ic_collect_filled);
            currentPost.setCollectCount(currentPost.getCollectCount() + 1);
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        } else {
            ivCollect.setImageResource(R.drawable.ic_collect_outline);
            currentPost.setCollectCount(currentPost.getCollectCount() - 1);
            Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
        }
        tvCollectCountBottom.setText(String.valueOf(currentPost.getCollectCount()));
    }

    private void toggleFollow() {
        if (currentPost == null || currentPost.isAnonymous()) return;
        boolean newState = !currentPost.isFollowed();
        currentPost.setFollowed(newState);
        btnFollow.setText(newState ? "已关注" : "关注");
        Toast.makeText(this, newState ? "关注成功" : "取消关注", Toast.LENGTH_SHORT).show();
    }

    private void sharePost() {
        if (currentPost == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                currentPost.getTitle() + "\n" + "https://yourapp.com/post/" + currentPost.getPostId());
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    private void scrollToReplies() {
        rvReplies.smoothScrollToPosition(0);
    }

    private void openUserProfile(int userId) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private int getCurrentUserId() {
        return com.androidcourse.moyan.utils.SharedPrefsHelper.getInstance().getUserId();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 从发布页面返回时刷新评论列表
        if (currentPost != null) {
            refreshReplies();
        }
    }
}