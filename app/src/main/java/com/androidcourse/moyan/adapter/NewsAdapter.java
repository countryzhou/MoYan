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
import com.androidcourse.moyan.utils.TimeUtils;

import java.util.List;

/**
 * 新闻列表适配器
 * 用于首页竖向新闻列表
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<NewsItem> newsList;
    private OnItemClickListener listener;

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
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // 防御性编程：检查数据有效性
        if (newsList == null || position < 0 || position >= newsList.size()) {
            return;
        }

        NewsItem news = newsList.get(position);
        if (news == null) {
            return;
        }

        holder.bind(news, listener);
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    public void updateData(List<NewsItem> newNewsList) {
        this.newsList = newNewsList;
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNewsImage;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvTime;
        TextView tvCommentCount;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            // 重新查找所有控件 - 确保每次都不为null
            ivNewsImage = itemView.findViewById(R.id.iv_news_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }

        public void bind(NewsItem news, OnItemClickListener listener) {
            if (news == null) {
                return;
            }

            // 【关键修复】每次bind时都重新查找控件，防止缓存问题
            // 这样即使构造函数中没找到，这里也能找到
            if (tvCommentCount == null) {
                tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            }
            if (tvTitle == null) {
                tvTitle = itemView.findViewById(R.id.tv_title);
            }
            if (tvAuthor == null) {
                tvAuthor = itemView.findViewById(R.id.tv_author);
            }
            if (tvTime == null) {
                tvTime = itemView.findViewById(R.id.tv_time);
            }
            if (ivNewsImage == null) {
                ivNewsImage = itemView.findViewById(R.id.iv_news_image);
            }

            // 设置标题 - 添加最终保护
            if (tvTitle != null) {
                String title = news.getTitle();
                tvTitle.setText(title != null ? title : "");
            }

            // 设置作者
            if (tvAuthor != null) {
                String author = news.getAuthor();
                tvAuthor.setText(author != null ? author : "");
            }

            // 设置时间
            if (tvTime != null) {
                long publishTime = news.getPublishTime();
                String timeStr = TimeUtils.formatRelativeTime(publishTime); // 使用相对时间更友好
                tvTime.setText(timeStr);
            }

            // 【最关键的修复】设置评论数 - 第91行
            // 即使 tvCommentCount 为 null，也不会崩溃
            if (tvCommentCount != null) {
                tvCommentCount.setText(String.valueOf(news.getCommentCount()));
            } else {
                // 如果还是null，输出错误但不崩溃
                android.util.Log.e("NewsAdapter", "tvCommentCount is still null after re-finding!");
            }

            // 设置图片
            if (ivNewsImage != null && news.getImageResId() != 0) {
                ivNewsImage.setImageResource(news.getImageResId());
            }

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(news);
                }
            });
        }
    }
}