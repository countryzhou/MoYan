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
import com.androidcourse.moyan.model.entity.NewsItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 新闻列表适配器
 * 功能：将新闻数据绑定到RecyclerView，展示新闻/帖子的列表项
 *
 * 使用场景：
 *   1. HomeActivity首页的新闻列表展示
 *   2. 为用户提供新闻/帖子的概览信息（标题、作者、发布时间、评论数）
 *   3. 支持点击跳转到新闻详情页
 *
 * 布局文件：item_news_card.xml（新闻卡片布局）
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    // ==================== 成员变量 ====================

    /** 上下文对象，用于加载布局和资源 */
    private Context context;

    /** 新闻数据列表 */
    private List<NewsItem> newsList;

    /** 列表项点击监听器 */
    private OnItemClickListener listener;

    /**
     * 列表项点击事件接口
     * 功能：定义新闻项被点击时的回调
     * 使用场景：由Activity/Fragment实现，用于跳转到详情页
     */
    public interface OnItemClickListener {
        /**
         * 当新闻项被点击时回调
         * @param newsItem 被点击的新闻对象，包含标题、ID等信息
         */
        void onItemClick(NewsItem newsItem);
    }

    /**
     * 功能：构造函数，初始化适配器
     * @param context 上下文对象，用于加载布局
     * @param newsList 新闻数据列表
     * @param listener 点击事件监听器
     */
    public NewsAdapter(Context context, List<NewsItem> newsList, OnItemClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    /**
     * 功能：创建ViewHolder，加载新闻卡片布局文件
     * @param parent 父视图容器
     * @param viewType 视图类型（本适配器只有一种类型）
     * @return NewsViewHolder实例
     */
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news_card, parent, false);
        return new NewsViewHolder(view);
    }

    /**
     * 功能：将新闻数据绑定到ViewHolder
     * @param holder ViewHolder实例
     * @param position 数据位置
     */
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem news = newsList.get(position);
        holder.bind(news, listener);
    }

    /**
     * 功能：获取新闻总数
     * @return 列表大小，如果列表为null则返回0
     */
    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    /**
     * 功能：批量更新新闻列表（全量刷新）
     * 具体实现：替换整个数据源并通知所有条目刷新
     * 使用场景：从服务器加载新数据后调用
     *
     * @param newNewsList 新的新闻列表数据
     */
    public void updateData(List<NewsItem> newNewsList) {
        this.newsList = newNewsList;
        notifyDataSetChanged();  // 刷新所有可见项
    }

    /**
     * ViewHolder内部类（静态类，避免内存泄漏）
     * 功能：缓存新闻卡片中的子View，提升列表滚动性能
     */
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        // ==================== UI组件声明 ====================
        private ImageView ivNewsImage;   // 新闻封面图片（可为空，部分新闻可能没有配图）
        private TextView tvTitle;         // 新闻标题
        // private TextView tvSummary;    // 新闻摘要（已注释，当前版本暂不使用）
        private TextView tvAuthor;        // 作者昵称
        private TextView tvTime;          // 发布时间
        // private TextView tvLikeCount;  // 点赞数（已注释，暂不显示）
        private TextView tvCommentCount;  // 评论数

        /**
         * 功能：ViewHolder构造函数，初始化所有UI组件
         * @param itemView 新闻卡片项的根视图
         */
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNewsImage = itemView.findViewById(R.id.iv_news_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            // tvSummary = itemView.findViewById(R.id.tv_summary);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvTime = itemView.findViewById(R.id.tv_time);
            // tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
        }

        /**
         * 功能：将新闻数据绑定到UI组件
         * 具体实现：
         *   1. 设置新闻封面图片（如果有图片资源ID）
         *   2. 设置新闻标题
         *   3. 设置作者名称
         *   4. 格式化并显示发布时间（格式：MM-dd HH:mm，如"12-25 14:30"）
         *   5. 设置评论数量
         *   6. 为整个itemView设置点击事件，触发外部监听器
         *
         * @param news 新闻数据对象
         * @param listener 点击事件监听器
         */
        public void bind(NewsItem news, OnItemClickListener listener) {
            // 设置封面图片（如果新闻对象包含图片资源ID）
            if (news.getImageResId() != 0) {
                ivNewsImage.setImageResource(news.getImageResId());
            }
            // 注意：如果图片是网络URL，此处应使用Glide等图片加载库
            // 示例：Glide.with(itemView.getContext()).load(news.getImageUrl()).into(ivNewsImage);

            // 设置标题
            tvTitle.setText(news.getTitle());

            // 设置作者（摘要字段当前未使用，已注释）
            tvAuthor.setText(news.getAuthor());

            // 格式化并设置发布时间
            // 使用SimpleDateFormat将时间戳转换为"月-日 时:分"格式
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            String timeStr = sdf.format(new Date(news.getPublishTime()));
            tvTime.setText(timeStr);

            // 设置互动数据（评论数）
            // 点赞数暂不显示，可根据需求后续添加
            tvCommentCount.setText(String.valueOf(news.getCommentCount()));

            // 设置整个卡片项的点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(news);  // 回调外部监听器
                }
            });
        }
    }
}