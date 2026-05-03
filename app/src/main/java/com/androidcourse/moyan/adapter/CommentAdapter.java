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
import com.androidcourse.moyan.model.Comment;
import com.androidcourse.moyan.utils.TimeUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 评论列表适配器
 * 支持匿名显示：匿名用户头像不可点击，显示匿名标记
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private int currentUserId;
    private OnCommentActionListener listener;

    public interface OnCommentActionListener {
        void onReplyClick(Comment comment);
        void onLikeClick(Comment comment, int position);
        void onDeleteClick(Comment comment, int position);
        void onAvatarClick(Comment comment);
    }

    public CommentAdapter(List<Comment> commentList, int currentUserId) {
        this.commentList = commentList;
        this.currentUserId = currentUserId;
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.listener = listener;
    }

    public void updateComments(List<Comment> newComments) {
        this.commentList.clear();
        this.commentList.addAll(newComments);
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        this.commentList.add(0, comment);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment, position);
    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivAvatar;
        private TextView tvNickname;
        private TextView tvContent;
        private TextView tvCommentTime;
        private ImageView ivLike;
        private TextView tvLikeCount;
        private TextView tvReplyText;
        private TextView tvReplyCount;
        private TextView tvDelete;
        private LinearLayout layoutReply;
        private LinearLayout layoutLike;
        private LinearLayout layoutReplyContent;
        private TextView tvReplyNickname;
        private TextView tvReplyContent;

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

        public void bind(Comment comment, int position) {
            // 设置昵称（支持匿名显示）
            if (comment.isAnonymous()) {
                tvNickname.setText("匿名用户");
            } else if (TextUtils.isEmpty(comment.getNickname())) {
                tvNickname.setText("用户" + comment.getUserId());
            } else {
                tvNickname.setText(comment.getNickname());
            }

            // 设置内容
            tvContent.setText(comment.getContent());

            // 设置时间
            tvCommentTime.setText(TimeUtils.formatRelativeTime(comment.getCreateTime()));

            // 设置点赞数
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));

            // 设置回复数
            tvReplyCount.setText(String.valueOf(comment.getReplyCount()));

            // 设置点赞状态
            updateLikeIcon(comment.isLiked());

            // 加载头像（匿名用户使用默认头像，且不可点击）
            if (comment.isProfileAccessible() && !TextUtils.isEmpty(comment.getAvatarUrl())) {
                Glide.with(itemView.getContext())
                        .load(comment.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }

            // 头像点击（匿名用户不可点击）
            ivAvatar.setOnClickListener(v -> {
                if (comment.isProfileAccessible() && listener != null) {
                    listener.onAvatarClick(comment);
                }
            });

            // 显示被回复的内容
            if (comment.getReplyTo() != null) {
                layoutReplyContent.setVisibility(View.VISIBLE);
                Comment replyTo = comment.getReplyTo();
                String replyName = replyTo.isAnonymous() ? "匿名用户" :
                        (TextUtils.isEmpty(replyTo.getNickname()) ? "用户" + replyTo.getUserId() : replyTo.getNickname());
                tvReplyNickname.setText("回复 @" + replyName + "：");
                tvReplyContent.setText(replyTo.getContent());
            } else {
                layoutReplyContent.setVisibility(View.GONE);
            }

            // 删除按钮（只有评论作者可见）
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

            // 回复按钮
            layoutReply.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReplyClick(comment);
                }
            });

            // 点赞按钮
            layoutLike.setOnClickListener(v -> toggleLike(comment, position));
        }

        private void updateLikeIcon(boolean isLiked) {
            if (isLiked) {
                ivLike.setImageResource(R.drawable.ic_like_outline);
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.colorAccent));
            } else {
                ivLike.setImageResource(R.drawable.ic_like_empty);
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        private void toggleLike(Comment comment, int position) {
            boolean newLikeState = !comment.isLiked();
            int newLikeCount = comment.getLikeCount() + (newLikeState ? 1 : -1);

            // 乐观更新UI
            comment.setLiked(newLikeState);
            comment.setLikeCount(newLikeCount);
            updateLikeIcon(newLikeState);
            tvLikeCount.setText(String.valueOf(newLikeCount));

            // 通过监听器通知上层处理网络请求
            if (listener != null) {
                listener.onLikeClick(comment, position);
            }
        }
    }
}