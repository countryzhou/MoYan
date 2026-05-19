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
import com.androidcourse.moyan.BuildConfig;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.androidcourse.moyan.R;

import com.androidcourse.moyan.adapter.CommentAdapter;
import com.androidcourse.moyan.model.Comment;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.utils.TimeUtils;
import com.androidcourse.moyan.viewmodel.PostDetailViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 帖子详情页面
 * 使用 PostDetailViewModel 处理数据加载和交互
 * 支持 BuildConfig.IS_DEBUG 切换 Mock/真实模式
 */
public class PostdetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private CircleImageView ivAuthorAvatar;
    private TextView tvAuthorName;
    private TextView tvPostTitle;
    private TextView tvPostContent;
    private ImageView ivPostImage;
    private TextView tvEditInfo;
    private TextView tvCommentCount;
    private RecyclerView rvComments;
    private LinearLayout layoutShare;
    private androidx.appcompat.widget.AppCompatButton btnFollow;
    private TextView tvTag1, tvTag2, tvTag3, tvTag4;
    private LinearLayout layoutTags;
    private EditText etComment;
    private ImageView ivLikeBottom;
    private TextView tvLikeCountBottom;
    private ImageView ivCommentBottom;
    private TextView tvCommentCountBottom;
    private ImageView ivCollect;
    private TextView ivCollectCountBottom;


    // 添加这一行
    private LinearLayout llImageContainer;  // 图片容器
    private Post currentPost;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private PostDetailViewModel viewModel;

    private boolean isLiked = false;
    private boolean isCollected = false;
    private boolean isFollowed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        viewModel = new PostDetailViewModel();
        initViews();
        setupListeners();
        loadPostData();
        loadComments();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivAuthorAvatar = findViewById(R.id.ivAuthorAvatar);
        tvAuthorName = findViewById(R.id.tvAuthorName);
        btnFollow = findViewById(R.id.btnFollow);
        layoutShare = findViewById(R.id.layoutShare);
        ivPostImage = findViewById(R.id.ivPostImage);
        tvPostTitle = findViewById(R.id.tvPostTitle);
        tvPostContent = findViewById(R.id.tvPostContent);
        tvEditInfo = findViewById(R.id.tvEditInfo);
        tvCommentCount = findViewById(R.id.tvCommentCount);
        layoutTags = findViewById(R.id.layoutTags);
        tvTag1 = findViewById(R.id.tvTag1);
        tvTag2 = findViewById(R.id.tvTag2);
        tvTag3 = findViewById(R.id.tvTag3);
        tvTag4 = findViewById(R.id.tvTag4);
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList, getCurrentUserId());
        rvComments.setAdapter(commentAdapter);
        etComment = findViewById(R.id.etComment);
        ivLikeBottom = findViewById(R.id.ivLikeBottom);
        tvLikeCountBottom = findViewById(R.id.tvLikeCountBottom);
        ivCommentBottom = findViewById(R.id.ivCommentBottom);
        tvCommentCountBottom = findViewById(R.id.tvCommentCountBottom);
        ivCollect = findViewById(R.id.ivCollect);
        ivCollectCountBottom = findViewById(R.id.ivCollectCountBottom);

        // 添加这一行：初始化图片容器
        llImageContainer = findViewById(R.id.llImageContainer);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        layoutShare.setOnClickListener(v -> sharePost());
        btnFollow.setOnClickListener(v -> toggleFollow());
        ivLikeBottom.setOnClickListener(v -> toggleLike());
        ivCollect.setOnClickListener(v -> toggleCollect());
        ivCommentBottom.setOnClickListener(v -> scrollToComments());

        // 评论适配器回调
        commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyClick(Comment comment) {
                etComment.setHint("回复 @" + comment.getDisplayName());
                etComment.requestFocus();
            }

            @Override
            public void onLikeClick(Comment comment, int position) {
                // 由适配器乐观更新，此处不需额外处理
            }

            @Override
            public void onDeleteClick(Comment comment, int position) {
                deleteComment(comment, position);
            }

            @Override
            public void onAvatarClick(Comment comment) {
                if (comment.isProfileAccessible()) {
                    openUserProfile(comment.getUserId());
                }
            }
        });

        etComment.setOnEditorActionListener((v, actionId, event) -> {
            submitComment();
            return true;
        });
    }
    // 在 PostDetailActivity 中添加图片展示方法
    private void displayPostImages(Post post) {
        // 确保控件已初始化
        if (llImageContainer == null) {
            return;
        }

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

                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.img_car_placeholder)
                        .into(imageView);

                llImageContainer.addView(imageView);
            }
        } else {
            llImageContainer.setVisibility(View.GONE);
        }
    }

    private void loadPostData() {
        int postId = getIntent().getIntExtra("post_id", 1);
        boolean isDebug = BuildConfig.IS_DEBUG;

        viewModel.loadPostDetail(postId, isDebug, new PostDetailViewModel.PostDetailCallback() {
            @Override
            public void onSuccess(Post post) {
                currentPost = post;
                displayPostData();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(PostdetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPostData() {
        if (currentPost == null) return;

        // 匿名处理
        if (currentPost.isAnonymous()) {
            tvAuthorName.setText(currentPost.getAnonymousName() != null ?
                    currentPost.getAnonymousName() : "匿名用户");
            ivAuthorAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            ivAuthorAvatar.setClickable(false);
            btnFollow.setVisibility(View.GONE);
        } else {
            tvAuthorName.setText(currentPost.getDisplayName());
            if (!TextUtils.isEmpty(currentPost.getAvatarUrl())) {
                Glide.with(this)
                        .load(currentPost.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(ivAuthorAvatar);
            }
            ivAuthorAvatar.setClickable(true);
            ivAuthorAvatar.setOnClickListener(v -> openUserProfile(currentPost.getUserId()));
        }

        tvPostTitle.setText(currentPost.getTitle());
        tvPostContent.setText(currentPost.getContent());
        tvLikeCountBottom.setText(String.valueOf(currentPost.getLikeCount()));
        tvCommentCount.setText("共" + currentPost.getReplyCount() + "条评论");
        tvCommentCountBottom.setText(String.valueOf(currentPost.getReplyCount()));
        tvEditInfo.setText("编辑于 " + TimeUtils.formatDateTime(currentPost.getUpdateTime()));
        displayTags(currentPost.getTags());

        isLiked = currentPost.isLiked();
        updateLikeUI();

        // 显示图片
        displayPostImages(currentPost);
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

    private void loadComments() {
        int postId = getIntent().getIntExtra("post_id", 1);
        viewModel.loadComments(postId, new PostDetailViewModel.CommentListCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentList.clear();
                commentList.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(PostdetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitComment() {
        String commentText = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        int postId = getIntent().getIntExtra("post_id", 1);
        viewModel.submitComment(postId, commentText, new PostDetailViewModel.SubmitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(PostdetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                etComment.setText("");
                loadComments();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(PostdetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteComment(Comment comment, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条评论吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 删除逻辑由适配器处理
                    commentList.remove(position);
                    commentAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void toggleLike() {
        if (currentPost == null) return;
        
        isLiked = !isLiked;
        updateLikeUI();
        
        // 乐观更新 UI
        if (isLiked) {
            int newCount = currentPost.getLikeCount() + 1;
            currentPost.setLikeCount(newCount);
            tvLikeCountBottom.setText(String.valueOf(newCount));
        } else {
            int newCount = currentPost.getLikeCount() - 1;
            currentPost.setLikeCount(newCount);
            tvLikeCountBottom.setText(String.valueOf(newCount));
        }
        
        // TODO: 同步到服务端
        // viewModel.toggleLike(currentPost.getPostId(), isLiked, callback);
        
    }

    private void updateLikeUI() {
        if (isLiked) {
            ivLikeBottom.setImageResource(R.drawable.ic_like_outline);
            ivLikeBottom.setColorFilter(getColor(R.color.colorAccent));
            tvLikeCountBottom.setTextColor(getColor(R.color.colorAccent));
        } else {
            ivLikeBottom.setImageResource(R.drawable.ic_like_empty);
            ivLikeBottom.clearColorFilter();
            tvLikeCountBottom.setTextColor(getColor(R.color.text_secondary));
        }
    }

    private void toggleCollect() {
        isCollected = !isCollected;
        if (isCollected) {
            ivCollect.setImageResource(R.drawable.ic_comment_filled);
            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        } else {
            ivCollect.setImageResource(R.drawable.ic_collect_outline);
            Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFollow() {
        if (currentPost == null || currentPost.isAnonymous()) return;
        isFollowed = !isFollowed;
        if (isFollowed) {
            btnFollow.setText("已关注");
            Toast.makeText(this, "关注成功", Toast.LENGTH_SHORT).show();
        } else {
            btnFollow.setText("关注");
            Toast.makeText(this, "取消关注", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePost() {
        if (currentPost == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                currentPost.getTitle() + "\n" + "https://yourapp.com/post/" + currentPost.getPostId());
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    private void scrollToComments() {
        rvComments.smoothScrollToPosition(0);
    }

    private void openUserProfile(int userId) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private int getCurrentUserId() {
        return com.androidcourse.moyan.utils.SharedPrefsHelper.getInstance().getUserId();
    }
}