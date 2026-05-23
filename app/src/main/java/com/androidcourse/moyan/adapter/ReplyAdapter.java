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
        private CircleImageView ivAvatar;
        private TextView tvNickname;
        private TextView tvContent;
        private TextView tvCommentTime;
        private ImageView ivLike;
        private TextView tvLikeCount;
        private TextView tvDelete;
        private LinearLayout layoutLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvNickname = itemView.findViewById(R.id.tvNickname);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            layoutLike = itemView.findViewById(R.id.layoutLike);
        }

        public void bind(Reply reply, int position) {
            // 设置昵称（支持匿名显示）
            if (reply.isAnonymous()) {
                tvNickname.setText("匿名用户");
            } else if (TextUtils.isEmpty(reply.getNickname())) {
                tvNickname.setText("用户" + reply.getUserId());
            } else {
                tvNickname.setText(reply.getNickname());
            }

            // 设置内容
            tvContent.setText(reply.getContent());

            // 设置时间
            tvCommentTime.setText(TimeUtils.formatRelativeTime(reply.getCreateTime()));

            // 设置点赞数
            tvLikeCount.setText(String.valueOf(reply.getLikeCount()));

            // 设置点赞状态
            updateLikeIcon(reply.isLiked());

            // 加载头像（匿名用户使用默认头像，且不可点击）
            if (reply.isProfileAccessible() && !TextUtils.isEmpty(reply.getAvatarUrl())) {
                Glide.with(itemView.getContext())
                        .load(reply.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }

            // 头像点击（匿名用户不可点击）
            ivAvatar.setOnClickListener(v -> {
                if (reply.isProfileAccessible() && listener != null) {
                    listener.onAvatarClick(reply);
                }
            });

            // 删除按钮（只有回复作者可见）
            if (reply.getUserId() == currentUserId) {
                tvDelete.setVisibility(View.VISIBLE);
                tvDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(reply, position);
                    }
                });
            } else {
                tvDelete.setVisibility(View.GONE);
            }

            // 点赞按钮
            layoutLike.setOnClickListener(v -> toggleLike(reply, position));
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

        private void toggleLike(Reply reply, int position) {
            boolean newLikeState = !reply.isLiked();
            int newLikeCount = reply.getLikeCount() + (newLikeState ? 1 : -1);

            // 乐观更新UI
            reply.setLiked(newLikeState);
            reply.setLikeCount(newLikeCount);
            updateLikeIcon(newLikeState);
            tvLikeCount.setText(String.valueOf(newLikeCount));

            // 通过监听器通知上层处理网络请求
            if (listener != null) {
                listener.onLikeClick(reply, position);
            }
        }
    }
}
