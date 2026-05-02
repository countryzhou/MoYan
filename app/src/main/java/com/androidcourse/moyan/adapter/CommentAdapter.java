package com.androidcourse.moyan.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.entity.Comment;
import com.androidcourse.moyan.network.CommentNetworkManager;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 评论列表适配器
 * 功能：将评论数据绑定到RecyclerView，支持评论的展示、点赞、回复、删除等交互操作
 *
 * 使用场景：
 *   1. 帖子详情页的评论列表展示
 *   2. 支持嵌套回复（显示"回复 @用户名"的引用内容）
 *   3. 评论的点赞状态切换
 *   4. 评论作者可见删除按钮
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    // ==================== 成员变量 ====================

    /** 评论数据列表 */
    private List<Comment> commentList;

    /** 当前登录用户的ID，用于判断是否为评论作者（决定是否显示删除按钮） */
    private int currentUserId;

    /** 评论交互监听器，用于将点击事件回调给Activity/Fragment处理 */
    private OnCommentActionListener listener;

    /**
     * 评论交互监听器接口
     * 功能：定义评论的各种操作回调，由外部（如PostDetailActivity）实现具体逻辑
     */
    public interface OnCommentActionListener {
        /**
         * 回复按钮点击回调
         * @param comment 被回复的评论对象
         */
        void onReplyClick(Comment comment);

        /**
         * 点赞按钮点击回调
         * @param comment 被操作的评论对象
         * @param position 评论在列表中的位置
         */
        void onLikeClick(Comment comment, int position);

        /**
         * 删除按钮点击回调
         * @param comment 要删除的评论对象
         * @param position 评论在列表中的位置
         */
        void onDeleteClick(Comment comment, int position);
    }

    /**
     * 功能：构造函数，初始化适配器
     * @param commentList 评论数据列表
     * @param currentUserId 当前登录用户ID
     */
    public CommentAdapter(List<Comment> commentList, int currentUserId) {
        this.commentList = commentList;
        this.currentUserId = currentUserId;
    }

    /**
     * 功能：设置评论交互监听器
     * @param listener 监听器实例
     */
    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.listener = listener;
    }

    /**
     * 功能：批量更新评论列表（全量刷新）
     * 具体实现：清空原有列表，添加新列表，并通知所有条目刷新
     * 使用场景：首次加载评论或下拉刷新时使用
     *
     * @param newComments 新的评论列表
     */
    public void updateComments(List<Comment> newComments) {
        this.commentList.clear();
        this.commentList.addAll(newComments);
        notifyDataSetChanged();
    }

    /**
     * 功能：在列表顶部添加一条新评论
     * 具体实现：在列表头部插入评论，并只刷新插入的位置（性能优化）
     * 使用场景：用户发表新评论成功后，立即显示在最上方
     *
     * @param comment 新发表的评论对象
     */
    public void addComment(Comment comment) {
        this.commentList.add(0, comment);
        notifyItemInserted(0);
    }

    /**
     * 功能：创建ViewHolder，加载评论项布局文件
     * @param parent 父视图容器
     * @param viewType 视图类型（本适配器只有一种类型）
     * @return ViewHolder实例
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_comment, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 功能：将数据绑定到ViewHolder
     * @param holder ViewHolder实例
     * @param position 数据位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment, position);
    }

    /**
     * 功能：获取评论总数
     * @return 列表大小，如果列表为null则返回0
     */
    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    /**
     * ViewHolder内部类
     * 功能：缓存评论项中的子View，避免重复findViewById，提升列表滚动性能
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        // ==================== UI组件声明 ====================
        private CircleImageView ivAvatar;      // 用户头像（圆形）
        private TextView tvNickname;            // 用户昵称
        private TextView tvContent;             // 评论内容
        private TextView tvCommentTime;         // 评论时间
        private ImageView ivLike;               // 点赞图标（爱心）
        private TextView tvLikeCount;           // 点赞数量
        private TextView tvReplyText;           // 回复按钮文字
        private TextView tvReplyCount;          // 回复数量
        private TextView tvDelete;              // 删除按钮（仅作者可见）
        private LinearLayout layoutReply;       // 回复按钮布局容器
        private LinearLayout layoutLike;        // 点赞按钮布局容器
        private LinearLayout layoutReplyContent; // 引用回复内容的容器（显示“回复 @xxx：原内容”）
        private TextView tvReplyNickname;       // 被回复者的昵称
        private TextView tvReplyContent;        // 被回复的原始内容

        /**
         * 功能：ViewHolder构造函数，初始化所有UI组件
         * @param itemView 评论项的根视图
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNickname = itemView.findViewById(R.id.tvNickname);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvReplyText = itemView.findViewById(R.id.tvReplyText);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            layoutReply = itemView.findViewById(R.id.layoutReply);
            layoutLike = itemView.findViewById(R.id.layoutLike);
            layoutReplyContent = itemView.findViewById(R.id.layoutReplyContent);
            tvReplyNickname = itemView.findViewById(R.id.tvReplyNickname);
            tvReplyContent = itemView.findViewById(R.id.tvReplyContent);
        }

        /**
         * 功能：将评论数据绑定到UI组件
         * 具体实现：
         *   1. 设置昵称（如果为空则显示"用户+ID"）
         *   2. 设置评论内容
         *   3. 格式化并显示时间
         *   4. 显示点赞数和回复数
         *   5. 根据点赞状态设置点赞图标颜色
         *   6. 使用Glide加载头像（支持缓存和占位图）
         *   7. 如果有被回复的评论，显示引用内容区域
         *   8. 如果是评论作者，显示删除按钮并设置点击事件
         *   9. 设置回复和点赞按钮的点击事件
         *
         * @param comment 评论数据对象
         * @param position 当前评论在列表中的位置
         */
        public void bind(Comment comment, int position) {
            // 设置昵称（如果为空则使用默认显示）
            if (TextUtils.isEmpty(comment.getNickname())) {
                tvNickname.setText("用户" + comment.getUserId());
            } else {
                tvNickname.setText(comment.getNickname());
            }

            // 设置评论内容
            tvContent.setText(comment.getContent());

            // 格式化并设置时间
            tvCommentTime.setText(formatTime(comment.getCreateTime()));

            // 设置点赞数量
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));

            // 设置回复数量（暂设为0，实际应从服务器获取或本地计算）
            tvReplyCount.setText("0");

            // 根据点赞状态更新点赞图标样式
            updateLikeIcon(comment.isLiked());

            // 加载用户头像（使用Glide图片加载库）
            if (!TextUtils.isEmpty(comment.getAvatarUrl())) {
                Glide.with(itemView.getContext())
                        .load(comment.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)  // 加载中占位图
                        .error(R.drawable.ic_avatar_placeholder)       // 加载失败占位图
                        .into(ivAvatar);
            } else {
                // 没有头像URL时使用默认头像
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }

            // 处理嵌套回复：显示被回复的原始评论内容
            if (comment.getReplyTo() != null) {
                layoutReplyContent.setVisibility(View.VISIBLE);
                Comment replyTo = comment.getReplyTo();
                tvReplyNickname.setText("回复 @" + replyTo.getNickname() + "：");
                tvReplyContent.setText(replyTo.getContent());
            } else {
                layoutReplyContent.setVisibility(View.GONE);
            }

            // 删除按钮：只有评论作者才能看到和操作
            if (comment.getUserId() == currentUserId) {
                tvDelete.setVisibility(View.VISIBLE);
                tvDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(comment, position);
                    }
                });
            } else {
                tvDelete.setVisibility(View.GONE);
            }

            // 回复按钮点击事件
            layoutReply.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReplyClick(comment);
                }
            });

            // 点赞按钮点击事件
            layoutLike.setOnClickListener(v -> toggleLike(comment, position));
        }

        /**
         * 功能：更新点赞图标的样式
         * 具体实现：
         *   - 已点赞：显示红色实心爱心，文字颜色为主题色
         *   - 未点赞：显示灰色空心爱心，文字颜色为次要文本色
         *
         * @param isLiked 当前是否已点赞
         */
        private void updateLikeIcon(boolean isLiked) {
            if (isLiked) {
                // 已点赞状态：红色爱心
                ivLike.setImageResource(R.drawable.ic_like_outline);
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.colorAccent));
            } else {
                // 未点赞状态：空心爱心
                ivLike.setImageResource(R.drawable.ic_like_empty);
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        /**
         * 功能：切换点赞状态（核心交互逻辑）
         * 具体实现：
         *   1. 计算新的点赞状态和点赞数
         *   2. 立即更新UI（乐观更新，提升用户体验）
         *   3. 发送网络请求给服务器
         *   4. 如果网络请求失败，回滚UI到原状态并提示用户
         *   5. 如果成功，回调通知外部（可选）
         *
         * 注意：采用乐观更新策略，先更新UI再发请求，失败时回滚
         *
         * @param comment 要操作的评论对象
         * @param position 评论位置（用于回调）
         */
        private void toggleLike(Comment comment, int position) {
            // 计算切换后的状态
            boolean newLikeState = !comment.isLiked();
            int newLikeCount = comment.getLikeCount() + (newLikeState ? 1 : -1);

            // 乐观更新：立即更新UI
            comment.setLiked(newLikeState);
            comment.setLikeCount(newLikeCount);
            updateLikeIcon(newLikeState);
            tvLikeCount.setText(String.valueOf(newLikeCount));

            // 发送网络请求
            boolean success = CommentNetworkManager.getInstance()
                    .likeComment(comment.getCommentId(), currentUserId, newLikeState);

            if (!success) {
                // 网络请求失败，回滚UI到原始状态
                comment.setLiked(!newLikeState);
                comment.setLikeCount(comment.getLikeCount() + (newLikeState ? -1 : 1));
                updateLikeIcon(!newLikeState);
                tvLikeCount.setText(String.valueOf(comment.getLikeCount()));

                // 提示用户操作失败
                Toast.makeText(itemView.getContext(), "操作失败，请重试", Toast.LENGTH_SHORT).show();
            } else if (listener != null) {
                // 成功时回调通知外部（可选，用于更新其他UI）
                listener.onLikeClick(comment, position);
            }
        }

        /**
         * 功能：格式化时间戳为友好的相对时间或绝对时间
         * 具体实现规则：
         *   - 小于1分钟：显示"刚刚"
         *   - 小于1小时：显示"X分钟前"
         *   - 小于24小时：显示"X小时前"
         *   - 超过24小时：显示"MM-dd"格式（如"12-25"）
         *
         * @param timestamp 时间戳（毫秒）
         * @return 格式化后的时间字符串
         */
        private String formatTime(long timestamp) {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;

            if (diff < 60 * 1000) {
                return "刚刚";
            } else if (diff < 60 * 60 * 1000) {
                return (diff / (60 * 1000)) + "分钟前";
            } else if (diff < 24 * 60 * 60 * 1000) {
                return (diff / (60 * 60 * 1000)) + "小时前";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
                return sdf.format(new Date(timestamp));
            }
        }
    }
}