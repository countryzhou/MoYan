package com.androidcourse.moyan.adapter;

import android.content.Context;
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

import java.util.List;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_card, parent, false);
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

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.iv_news_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }

        public void bind(Post post, OnPostClickListener listener) {
            tvTitle.setText(post.getTitle());
            tvAuthor.setText(post.getDisplayName());
            tvTime.setText(TimeUtils.formatRelativeTime(post.getPostTime()));
            tvCommentCount.setText(String.valueOf(post.getReplyCount()));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPostClick(post);
            });
        }
    }
}