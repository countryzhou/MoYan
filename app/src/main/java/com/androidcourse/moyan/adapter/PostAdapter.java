package com.androidcourse.moyan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.utils.TimeUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 帖子列表通用适配器
 * 用于搜索结果、用户主页帖子列表等场景
 * 支持匿名显示：匿名用户头像不可点击，显示匿名名称
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onAvatarClick(Post post);
    }

    public PostAdapter(Context context, List<Post> postList, OnPostClickListener listener) {
        this.context = context;
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news_card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post, listener);
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    public void updateData(List<Post> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostImage;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvTime;
        TextView tvCommentCount;
        CircleImageView ivAvatar;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.iv_news_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            // 注意：如果item_news_card布局没有头像，需要添加或适配
        }

        public void bind(Post post, OnPostClickListener listener) {
            tvTitle.setText(post.getTitle());

            // 匿名显示处理
            if (post.isAnonymous()) {
                tvAuthor.setText(post.getAnonymousName() != null ?
                        post.getAnonymousName() : "匿名用户");
            } else {
                tvAuthor.setText(post.getDisplayName());
            }

            tvTime.setText(TimeUtils.formatDateTime(post.getCreateTime()));
            tvCommentCount.setText(String.valueOf(post.getReplyCount()));

            // 头像加载（匿名用户使用默认头像）
            if (post.isProfileAccessible() && !TextUtils.isEmpty(post.getAvatarUrl())) {
                Glide.with(itemView.getContext())
                        .load(post.getAvatarUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(ivAvatar != null ? ivAvatar : null);
            }

            // 帖子点击
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPostClick(post);
            });

            // 头像点击（匿名用户不可点击）
            if (ivAvatar != null) {
                ivAvatar.setOnClickListener(v -> {
                    if (post.isProfileAccessible() && listener != null) {
                        listener.onAvatarClick(post);
                    }
                });
            }
        }
    }
}