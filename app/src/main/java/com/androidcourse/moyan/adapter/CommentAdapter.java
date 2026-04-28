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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.Comment;
import com.androidcourse.moyan.network.CommentNetworkManager;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Comment> commentList;
    private int currentUserId;
    private OnCommentActionListener listener;

    public interface OnCommentActionListener {
        void onReplyClick(Comment comment);
        void onLikeClick(Comment comment, int position);
        void onDeleteClick(Comment comment, int position);
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
            // 设置昵称
            if (TextUtils.isEmpty(comment.getNickname())) {
                tvNickname.setText("用户" + comment.getUserId());
            } else {
                tvNickname.setText(comment.getNickname());
            }

            // 设置内容
            tvContent.setText(comment.getContent());

            // 设置时间
            tvCommentTime.setText(formatTime(comment.getCreateTime()));

            // 设置点赞数
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));

            // 设置回复数（暂时设置为0，后续可从服务器获取）
            tvReplyCount.setText("0");

            // 设置点赞状态
            updateLikeIcon(comment.isLiked());

            // 加载头像
            if (!TextUtils.isEmpty(comment.getAvatarUrl())) {
                Glide.with(itemView.getContext())
                        .load(comment.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }

            // 显示被回复的内容（如果是回复其他评论）
            if (comment.getReplyTo() != null) {
                layoutReplyContent.setVisibility(View.VISIBLE);
                Comment replyTo = comment.getReplyTo();
                tvReplyNickname.setText("回复 @" + replyTo.getNickname() + "：");
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
                // 已点赞：显示红色爱心
                ivLike.setImageResource(R.drawable.ic_like_outline);
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.colorAccent));
            } else {
                // 未点赞：显示空心爱心
                ivLike.setImageResource(R.drawable.ic_like_empty);
                tvLikeCount.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        private void toggleLike(Comment comment, int position) {
            boolean newLikeState = !comment.isLiked();
            int newLikeCount = comment.getLikeCount() + (newLikeState ? 1 : -1);

            // 更新UI
            comment.setLiked(newLikeState);
            comment.setLikeCount(newLikeCount);
            updateLikeIcon(newLikeState);
            tvLikeCount.setText(String.valueOf(newLikeCount));

            // 调用网络请求
            boolean success = CommentNetworkManager.getInstance()
                    .likeComment(comment.getCommentId(), currentUserId, newLikeState);

            if (!success) {
                // 恢复原状态
                comment.setLiked(!newLikeState);
                comment.setLikeCount(comment.getLikeCount() + (newLikeState ? -1 : 1));
                updateLikeIcon(!newLikeState);
                tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
                Toast.makeText(itemView.getContext(), "操作失败，请重试", Toast.LENGTH_SHORT).show();
            } else if (listener != null) {
                listener.onLikeClick(comment, position);
            }
        }

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