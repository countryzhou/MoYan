package com.androidcourse.moyan.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.Reply;
import com.androidcourse.moyan.utils.TimeUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 回复列表适配器
 * 支持匿名显示：匿名用户头像不可点击，显示匿名标记
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private List<Reply> replyList;
    private int currentUserId;
    private OnReplyActionListener listener;

    public interface OnReplyActionListener {
        void onReplyClick(Reply reply);
        void onLikeClick(Reply reply, int position);
        void onDeleteClick(Reply reply, int position);
        void onAvatarClick(Reply reply);
    }

    public ReplyAdapter(List<Reply> replyList, int currentUserId) {
        this.replyList = replyList;
        this.currentUserId = currentUserId;
    }

    public void setOnReplyActionListener(OnReplyActionListener listener) {
        this.listener = listener;
    }

    public void updateReplies(List<Reply> newReplies) {
        this.replyList.clear();
        this.replyList.addAll(newReplies);
        notifyDataSetChanged();
    }

    public void addReply(Reply reply) {
        this.replyList.add(0, reply);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reply reply = replyList.get(position);
        holder.bind(reply, position);
    }

    @Override
    public int getItemCount() {
        return replyList == null ? 0 : replyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // ✅ 修正：使用布局中正确的 ID
        private CircleImageView ivReplyAvatar;
        private TextView tvReplyNickname;
        private TextView tvReplyContent;
        private TextView tvReplyTime;
        private ImageView ivReplyLike;
        private TextView tvReplyLikeCount;
        private TextView tvReplyDelete;
        private LinearLayout layoutReplyLike;
        private TextView tvReplyToReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // ✅ 使用正确的 ID
            ivReplyAvatar = itemView.findViewById(R.id.ivReplyAvatar);
            tvReplyNickname = itemView.findViewById(R.id.tvReplyNickname);
            tvReplyContent = itemView.findViewById(R.id.tvReplyContent);
            tvReplyTime = itemView.findViewById(R.id.tvReplyTime);
            ivReplyLike = itemView.findViewById(R.id.ivReplyLike);
            tvReplyLikeCount = itemView.findViewById(R.id.tvReplyLikeCount);
            tvReplyDelete = itemView.findViewById(R.id.tvReplyDelete);
            layoutReplyLike = itemView.findViewById(R.id.layoutReplyLike);
            tvReplyToReply = itemView.findViewById(R.id.tvReplyToReply);
        }

        public void bind(Reply reply, int position) {
            // 设置昵称（支持匿名显示）
            if (reply.isAnonymous()) {
                tvReplyNickname.setText("匿名用户");
                tvReplyToReply.setVisibility(View.GONE);
            } else if (TextUtils.isEmpty(reply.getNickname())) {
                tvReplyNickname.setText("用户" + reply.getUserId());
            } else {
                tvReplyNickname.setText(reply.getNickname());
            }

            // 设置内容
            tvReplyContent.setText(reply.getContent());

            // 设置时间
            tvReplyTime.setText(TimeUtils.formatRelativeTime(reply.getCreateTime()));

            // 设置点赞数
            tvReplyLikeCount.setText(String.valueOf(reply.getLikeCount()));

            // 设置点赞状态
            updateLikeIcon(reply.isLiked());

            // 加载头像（匿名用户使用默认头像，且不可点击）
            if (reply.isProfileAccessible() && !TextUtils.isEmpty(reply.getAvatarUrl())) {
                Glide.with(itemView.getContext())
                        .load(reply.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(ivReplyAvatar);
            } else {
                ivReplyAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }

            // 头像点击（匿名用户不可点击）
            ivReplyAvatar.setOnClickListener(v -> {
                if (reply.isProfileAccessible() && listener != null) {
                    listener.onAvatarClick(reply);
                }
            });

            // 删除按钮（只有回复作者可见）
            if (reply.getUserId() == currentUserId) {
                tvReplyDelete.setVisibility(View.VISIBLE);
                tvReplyDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(reply, position);
                    }
                });
            } else {
                tvReplyDelete.setVisibility(View.GONE);
            }

            // 点赞按钮
            layoutReplyLike.setOnClickListener(v -> toggleLike(reply, position));
        }

        private void updateLikeIcon(boolean isLiked) {
            if (isLiked) {
                ivReplyLike.setImageResource(R.drawable.ic_like_outline);
                tvReplyLikeCount.setTextColor(itemView.getContext().getColor(R.color.colorAccent));
            } else {
                ivReplyLike.setImageResource(R.drawable.ic_like_empty);
                tvReplyLikeCount.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }
        }

        private void toggleLike(Reply reply, int position) {
            boolean newLikeState = !reply.isLiked();
            int newLikeCount = reply.getLikeCount() + (newLikeState ? 1 : -1);

            reply.setLiked(newLikeState);
            reply.setLikeCount(newLikeCount);
            updateLikeIcon(newLikeState);
            tvReplyLikeCount.setText(String.valueOf(newLikeCount));

            if (listener != null) {
                listener.onLikeClick(reply, position);
            }
        }
    }
}