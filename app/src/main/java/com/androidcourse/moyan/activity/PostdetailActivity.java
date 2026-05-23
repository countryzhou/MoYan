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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    // 互动区
    private ImageView ivLikeBottom;
    private TextView tvLikeCountBottom;
    private ImageView ivCommentBottom;
    private TextView tvCommentCountBottom;
    private ImageView ivCollect;
    private TextView ivCollectCountBottom;
    private LinearLayout layoutShare;

    // 回复区
    private RecyclerView rvReplies;
    private EditText etComment;

    private Post currentPost;
    private ReplyAdapter replyAdapter;
    private List<Reply> replyList = new ArrayList<>();
    private PostDetailViewModel viewModel;

    private boolean isLiked = false;
    private boolean isCollected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        viewModel = new PostDetailViewModel();
        initViews();
        setupListeners();
        loadPostData();
        loadReplies();
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

        ivLikeBottom = findViewById(R.id.ivLikeBottom);
        tvLikeCountBottom = findViewById(R.id.tvLikeCountBottom);
        ivCommentBottom = findViewById(R.id.ivCommentBottom);
        tvCommentCountBottom = findViewById(R.id.tvCommentCountBottom);
        ivCollect = findViewById(R.id.ivCollect);
        ivCollectCountBottom = findViewById(R.id.ivCollectCountBottom);
        layoutShare = findViewById(R.id.layoutShare);

        rvReplies = findViewById(R.id.rvComments);
        rvReplies.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new ReplyAdapter(replyList, getCurrentUserId());
        rvReplies.setAdapter(replyAdapter);
        etComment = findViewById(R.id.etComment);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        layoutShare.setOnClickListener(v -> sharePost());
        btnFollow.setOnClickListener(v -> toggleFollow());
        ivLikeBottom.setOnClickListener(v -> toggleLike());
        ivCollect.setOnClickListener(v -> toggleCollect());
        ivCommentBottom.setOnClickListener(v -> scrollToReplies());

        replyAdapter.setOnReplyActionListener(new ReplyAdapter.OnReplyActionListener() {
            @Override
            public void onReplyClick(Reply reply) {
                Intent intent = new Intent(PostdetailActivity.this, CreateReplyActivity.class);
                intent.putExtra("post_id", currentPost != null ? currentPost.getPostId() : -1);
                startActivity(intent);
            }

            @Override
            public void onLikeClick(Reply reply, int position) {
                // TODO: 点赞回复
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

        etComment.setOnClickListener(v -> {
            Intent intent = new Intent(PostdetailActivity.this, CreateReplyActivity.class);
            intent.putExtra("post_id", currentPost != null ? currentPost.getPostId() : -1);
            startActivity(intent);
        });
    }

    private void loadPostData() {
        int postId = getIntent().getIntExtra("post_id", 1);
        viewModel.loadPostDetail(postId, new PostDetailViewModel.PostDetailCallback() {
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

        if (currentPost.isAnonymous()) {
            tvAuthorName.setText(currentPost.getAnonymousName() != null ? currentPost.getAnonymousName() : "匿名用户");
            ivAuthorAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            ivAuthorAvatar.setClickable(false);
            btnFollow.setVisibility(View.GONE);
        } else {
            tvAuthorName.setText(currentPost.getDisplayName());
            if (!TextUtils.isEmpty(currentPost.getAvatarUrl())) {
                Glide.with(this).load(currentPost.getAvatarUrl()).placeholder(R.drawable.ic_avatar_placeholder).into(ivAuthorAvatar);
            }
            ivAuthorAvatar.setClickable(true);
            ivAuthorAvatar.setOnClickListener(v -> openUserProfile(currentPost.getUserId()));
        }

        tvPostTitle.setText(currentPost.getTitle());
        tvPostContent.setText(currentPost.getContent());

        tvLikeCountBottom.setText(String.valueOf(currentPost.getLikeCount()));
        tvCommentCountBottom.setText(String.valueOf(currentPost.getReplyCount()));
        ivCollectCountBottom.setText(String.valueOf(currentPost.getCollectCount()));

        tvEditInfo.setText("编辑于 " + TimeUtils.formatDateTime(currentPost.getUpdateTime()));
        displayTags(currentPost.getTags());

        isLiked = currentPost.isLiked();
        updateLikeUI();

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
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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

    private void loadReplies() {
        int postId = getIntent().getIntExtra("post_id", 1);
        viewModel.loadReplies(postId, new PostDetailViewModel.ReplyListCallback() {
            @Override
            public void onSuccess(List<Reply> replies) {
                replyList.clear();
                replyList.addAll(replies);
                replyAdapter.notifyDataSetChanged();

                if (currentPost != null) {
                    currentPost.setReplyCount(replies.size());
                    tvCommentCountBottom.setText(String.valueOf(replies.size()));
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(PostdetailActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReply(Reply reply, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条回复吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    replyList.remove(position);
                    replyAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void toggleLike() {
        if (currentPost == null) return;
        isLiked = !isLiked;
        updateLikeUI();

        if (isLiked) {
            currentPost.setLikeCount(currentPost.getLikeCount() + 1);
        } else {
            currentPost.setLikeCount(currentPost.getLikeCount() - 1);
        }
        tvLikeCountBottom.setText(String.valueOf(currentPost.getLikeCount()));
    }

    private void updateLikeUI() {
        if (isLiked) {
            ivLikeBottom.setImageResource(R.drawable.ic_like_filled);
            ivLikeBottom.setColorFilter(getColor(R.color.colorAccent));
        } else {
            ivLikeBottom.setImageResource(R.drawable.ic_like_outline);
            ivLikeBottom.clearColorFilter();
        }
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
        ivCollectCountBottom.setText(String.valueOf(currentPost.getCollectCount()));
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentPost.getTitle() + "\n" + "https://yourapp.com/post/" + currentPost.getPostId());
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
}