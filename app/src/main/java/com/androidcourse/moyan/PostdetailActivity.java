package com.androidcourse.moyan;

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

import com.androidcourse.moyan.adapter.CommentAdapter;
import com.androidcourse.moyan.model.Comment;
import com.androidcourse.moyan.model.Post;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostdetailActivity extends AppCompatActivity {

    // UI组件
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

    // 标签组件
    private TextView tvTag1, tvTag2, tvTag3, tvTag4;
    private LinearLayout layoutTags;

    // 底部栏组件
    private EditText etComment;
    private ImageView ivLikeBottom;
    private TextView tvLikeCountBottom;
    private ImageView ivCommentBottom;
    private TextView tvCommentCountBottom;
    private ImageView ivCollect;
    private TextView ivCollectCountBottom;

    // 数据
    private Post currentPost;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();

    // 状态
    private boolean isLiked = false;
    private boolean isCollected = false;
    private boolean isFollowed = false;
    private int currentUserId = 1; // 假设当前登录用户ID为1，实际应从SharedPreferences获取

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        initViews();
        setupListeners();

        // 获取文章数据（从Intent或网络）
        loadPostData();
        loadComments();
    }

    private void initViews() {
        // 顶部栏
        btnBack = findViewById(R.id.btnBack);
        ivAuthorAvatar = findViewById(R.id.ivAuthorAvatar);
        tvAuthorName = findViewById(R.id.tvAuthorName);
        btnFollow = findViewById(R.id.btnFollow);
        layoutShare = findViewById(R.id.layoutShare);

        // 内容区
        ivPostImage = findViewById(R.id.ivPostImage);
        tvPostTitle = findViewById(R.id.tvPostTitle);
        tvPostContent = findViewById(R.id.tvPostContent);
        tvEditInfo = findViewById(R.id.tvEditInfo);
        tvCommentCount = findViewById(R.id.tvCommentCount);

        // 标签
        layoutTags = findViewById(R.id.layoutTags);
        tvTag1 = findViewById(R.id.tvTag1);
        tvTag2 = findViewById(R.id.tvTag2);
        tvTag3 = findViewById(R.id.tvTag3);
        tvTag4 = findViewById(R.id.tvTag4);

        // 评论列表
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setAdapter(commentAdapter);

        // 底部栏
        etComment = findViewById(R.id.etComment);
        ivLikeBottom = findViewById(R.id.ivLikeBottom);
        tvLikeCountBottom = findViewById(R.id.tvLikeCountBottom);
        ivCommentBottom = findViewById(R.id.ivCommentBottom);
        tvCommentCountBottom = findViewById(R.id.tvCommentCountBottom);
        ivCollect = findViewById(R.id.ivCollect);
        ivCollectCountBottom = findViewById(R.id.ivCollectCountBottom);
    }

    private void setupListeners() {
        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 分享按钮
        layoutShare.setOnClickListener(v -> sharePost());

        // 关注按钮
        btnFollow.setOnClickListener(v -> toggleFollow());

        // 点赞按钮
        ivLikeBottom.setOnClickListener(v -> toggleLike());

        // 收藏按钮
        ivCollect.setOnClickListener(v -> toggleCollect());

        // 评论按钮（滚动到评论区）
        ivCommentBottom.setOnClickListener(v -> scrollToComments());

        // 发表评论
        etComment.setOnEditorActionListener((v, actionId, event) -> {
            submitComment();
            return true;
        });
    }

    private void loadPostData() {
        // 模拟从服务器获取数据
        // 实际应该调用API: GET /api/post/{postId}

        currentPost = getMockPost(); // 临时使用模拟数据

        if (currentPost != null) {
            displayPostData();
        }
    }

    private void displayPostData() {
        if (currentPost == null) return;

        // 处理匿名逻辑
        boolean isAnonymous = currentPost.isAnonymous();

        if (isAnonymous) {
            // 匿名显示：昵称显示为"匿名用户"，使用默认头像
            tvAuthorName.setText("匿名用户");
            ivAuthorAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            btnFollow.setVisibility(View.GONE); // 匿名用户不可关注
        } else {
            // 正常显示
            tvAuthorName.setText(TextUtils.isEmpty(currentPost.getNickname()) ?
                    "用户" + currentPost.getUserId() : currentPost.getNickname());

            // 加载头像（如果有avatarUrl）
            if (!TextUtils.isEmpty(currentPost.getAvatarUrl())) {
                Glide.with(this)
                        .load(currentPost.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(ivAuthorAvatar);
            }

            // 检查是否已关注
            checkFollowStatus();
        }

        // 标题
        tvPostTitle.setText(currentPost.getTitle());

        // 内容
        tvPostContent.setText(currentPost.getContent());

        // 点赞数
        tvLikeCountBottom.setText(String.valueOf(currentPost.getLikeCount()));
        tvLikeCountBottom.setText(String.valueOf(currentPost.getLikeCount()));

        // 评论数
        int commentCount = currentPost.getReplyCount();
        tvCommentCount.setText("共" + commentCount + "条评论");
        tvCommentCountBottom.setText(String.valueOf(commentCount));

        // 收藏数（模拟）
        int collectCount = (int)(Math.random() * 100);
        ivCollectCountBottom.setText(String.valueOf(collectCount));

        // 编辑时间
        String editTime = formatTime(currentPost.getUpdateTime());
        tvEditInfo.setText("编辑于 " + editTime);

        // 处理标签
        displayTags(currentPost.getTags());

        // 检查点赞状态
        isLiked = currentPost.isLiked();
        updateLikeUI();

        // 加载文章图片（模拟）
        loadPostImage();
    }

    /**
     * 显示标签
     * 标签格式：逗号分隔，如 "标签1,标签2,标签3"
     */
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
        // 模拟加载评论数据
        // 实际应该调用API: GET /api/post/{postId}/comments

        commentList.clear();
        commentList.addAll(getMockComments());
        commentAdapter.notifyDataSetChanged();
    }

    private void toggleLike() {
        isLiked = !isLiked;
        updateLikeUI();

        // 调用API: POST /api/post/like
        // 实际请求代码...

        if (isLiked) {
            int newCount = currentPost.getLikeCount() + 1;
            currentPost.setLikeCount(newCount);
            tvLikeCountBottom.setText(String.valueOf(newCount));
            Toast.makeText(this, "点赞成功", Toast.LENGTH_SHORT).show();
        } else {
            int newCount = currentPost.getLikeCount() - 1;
            currentPost.setLikeCount(newCount);
            tvLikeCountBottom.setText(String.valueOf(newCount));
            Toast.makeText(this, "取消点赞", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLikeUI() {
        if (isLiked) {
            ivLikeBottom.setImageResource(R.drawable.ic_like_outline);
            tvLikeCountBottom.setTextColor(getColor(R.color.colorAccent));
        } else {
            ivLikeBottom.setImageResource(R.drawable.ic_like_empty);
            tvLikeCountBottom.setTextColor(getColor(R.color.text_secondary));
        }
    }

    private void toggleCollect() {
        isCollected = !isCollected;

        // 调用API: POST /api/post/collect

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

        // 调用API: POST /api/user/follow

        if (isFollowed) {
            btnFollow.setText("已关注");
            btnFollow.setBackgroundResource(R.drawable.bg_btn_followed);
            Toast.makeText(this, "关注成功", Toast.LENGTH_SHORT).show();
        } else {
            btnFollow.setText("关注");
            btnFollow.setBackgroundResource(R.drawable.bg_btn_follow_selector);
            Toast.makeText(this, "取消关注", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFollowStatus() {
        // 调用API: GET /api/user/follow/status
        // 模拟数据
        isFollowed = false;
        if (!isFollowed) {
            btnFollow.setText("关注");
        } else {
            btnFollow.setText("已关注");
        }
    }

    private void submitComment() {
        String commentText = etComment.getText().toString().trim();
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用API: POST /api/post/{postId}/comment

        // 模拟添加评论
        Comment newComment = new Comment();
        newComment.setContent(commentText);
        newComment.setUserId(currentUserId);
        newComment.setNickname("当前用户");
        newComment.setCreateTime(System.currentTimeMillis());

        commentList.add(0, newComment);
        commentAdapter.notifyItemInserted(0);
        rvComments.smoothScrollToPosition(0);

        // 更新评论数
        int newCount = currentPost.getReplyCount() + 1;
        currentPost.setReplyCount(newCount);
        tvCommentCount.setText("共" + newCount + "条评论");
        tvCommentCountBottom.setText(String.valueOf(newCount));

        etComment.setText("");
        Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
    }

    private void sharePost() {
        if (currentPost == null) return;

        // 分享功能
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentPost.getTitle() + "\n" +
                "https://yourapp.com/post/" + currentPost.getPostId());
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    private void scrollToComments() {
        rvComments.smoothScrollToPosition(0);
    }

    private void loadPostImage() {
        // 根据文章内容加载相应图片（实际应从服务器获取图片URL）
        // 这里使用默认图片
        Glide.with(this)
                .load(R.drawable.img_car_placeholder)
                .into(ivPostImage);
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // ==================== 模拟数据 ====================

    private Post getMockPost() {
        Post post = new Post();
        post.setPostId(1);
        post.setUserId(100);
        post.setTitle("林丹7000万豪宅曝光！内部装修如皇宫");
        post.setContent("北京时间1月10日消息，林丹在老家福建的别墅曝光，这套豪宅位于富人区，均价11万！而从面积来看，在600-800平方米，因此，这套豪宅在7000万人民币左右。当然，豪宅内部装修也相当豪华，就像是皇宫一样。当然，林丹打了20年球，靠打球的收入就超过2亿。因此，7000万对林丹来说不算什么。");
        post.setTags("体育,明星,豪宅");
        post.setAnonymous(false); // 是否匿名
        post.setLikeCount(279);
        post.setReplyCount(18);
        post.setViewCount(10000);
        post.setCreateTime(System.currentTimeMillis() - 3600000);
        post.setUpdateTime(System.currentTimeMillis() - 1800000);
        post.setNickname("郭敬明");
        post.setAvatarUrl(null);
        post.setLiked(false);
        return post;
    }

    private List<Comment> getMockComments() {
        List<Comment> comments = new ArrayList<>();

        Comment comment1 = new Comment();
        comment1.setCommentId(1);
        comment1.setContent("林丹太厉害了！这豪宅真不错");
        comment1.setUserId(2);
        comment1.setNickname("体育爱好者");
        comment1.setLikeCount(25);
        comment1.setCreateTime(System.currentTimeMillis() - 3600000);
        comments.add(comment1);

        Comment comment2 = new Comment();
        comment2.setCommentId(2);
        comment2.setContent("20年赚2亿，平均一年1000万，确实厉害");
        comment2.setUserId(3);
        comment2.setNickname("吃瓜群众");
        comment2.setLikeCount(12);
        comment2.setCreateTime(System.currentTimeMillis() - 7200000);
        comments.add(comment2);

        return comments;
    }
}