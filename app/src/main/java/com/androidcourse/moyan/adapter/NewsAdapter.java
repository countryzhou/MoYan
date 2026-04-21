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
import com.androidcourse.moyan.model.NewsItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<NewsItem> newsList;
    private OnItemClickListener listener;

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public NewsAdapter(Context context, List<NewsItem> newsList, OnItemClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem news = newsList.get(position);
        holder.bind(news, listener);
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    // 更新数据的方法
    public void updateData(List<NewsItem> newNewsList) {
        this.newsList = newNewsList;
        notifyDataSetChanged();
    }

    // ViewHolder 内部类
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNewsImage;
        TextView tvTitle;
        //TextView tvSummary;
        TextView tvAuthor;
        TextView tvTime;
        ///TextView tvLikeCount;
        TextView tvCommentCount;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNewsImage = itemView.findViewById(R.id.iv_news_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            //tvSummary = itemView.findViewById(R.id.tv_summary);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvTime = itemView.findViewById(R.id.tv_time);
            //tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }

        public void bind(NewsItem news, OnItemClickListener listener) {
            // 设置数据
            if (news.getImageResId() != 0) {
                ivNewsImage.setImageResource(news.getImageResId());
            }
            tvTitle.setText(news.getTitle());
            //tvSummary.setText(news.getSummary());
            tvAuthor.setText(news.getAuthor());

            // 格式化时间显示
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            String timeStr = sdf.format(new Date(news.getPublishTime()));
            tvTime.setText(timeStr);

            // 设置点赞和评论数
            //tvLikeCount.setText(String.valueOf(news.getLikeCount()));
            tvCommentCount.setText(String.valueOf(news.getCommentCount()));

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(news);
                }
            });
        }
    }
}