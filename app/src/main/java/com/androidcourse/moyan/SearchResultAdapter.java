package com.androidcourse.moyan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<SearchResult> searchResults;

    public SearchResultAdapter(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult result = searchResults.get(position);
        holder.tvUsername.setText(result.getUsername());
        holder.tvUserInfo.setText(result.getUserInfo());
        holder.tvContent.setText(result.getContent());
        holder.tvTags.setText(result.getTags());
        holder.tvTime.setText(result.getTime());
        holder.tvLikes.setText(result.getLikes());
        holder.tvComments.setText(result.getComments());
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        ImageView ivContent;
        TextView tvUsername;
        TextView tvUserInfo;
        TextView tvContent;
        TextView tvTags;
        TextView tvTime;
        TextView tvLikes;
        TextView tvComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivContent = itemView.findViewById(R.id.iv_content);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvUserInfo = itemView.findViewById(R.id.tv_user_info);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTags = itemView.findViewById(R.id.tv_tags);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            tvComments = itemView.findViewById(R.id.tv_comments);
        }
    }

    public static class SearchResult {
        private String username;
        private String userInfo;
        private String content;
        private String tags;
        private String time;
        private String likes;
        private String comments;

        public SearchResult(String username, String userInfo, String content, String tags, String time, String likes, String comments) {
            this.username = username;
            this.userInfo = userInfo;
            this.content = content;
            this.tags = tags;
            this.time = time;
            this.likes = likes;
            this.comments = comments;
        }

        public String getUsername() {
            return username;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public String getContent() {
            return content;
        }

        public String getTags() {
            return tags;
        }

        public String getTime() {
            return time;
        }

        public String getLikes() {
            return likes;
        }

        public String getComments() {
            return comments;
        }
    }
}
