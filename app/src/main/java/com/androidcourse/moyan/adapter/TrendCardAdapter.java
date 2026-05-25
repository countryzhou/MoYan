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
import com.androidcourse.moyan.model.TrendCard;

import java.util.List;

/**
 * 趋势卡片适配器
 * 用于首页横向滚动推荐区域（支持无限循环）
 */
public class TrendCardAdapter extends RecyclerView.Adapter<TrendCardAdapter.TrendViewHolder> {

    private Context context;
    private List<TrendCard> trendList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TrendCard trendCard);
    }

    public TrendCardAdapter(Context context, List<TrendCard> trendList, OnItemClickListener listener) {
        this.context = context;
        this.trendList = trendList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_trend_card, parent, false);
        return new TrendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendViewHolder holder, int position) {
        TrendCard trend = trendList.get(position);
        holder.bind(trend, listener);
    }

    @Override
    public int getItemCount() {
        return trendList == null ? 0 : trendList.size();
    }

    public void updateData(List<TrendCard> newTrendList) {
        this.trendList = newTrendList;
        notifyDataSetChanged();
    }

    static class TrendViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvCommentCount;
        TextView tvTime;
        ImageView ivTrendImage;

        public TrendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivTrendImage = itemView.findViewById(R.id.iv_trend_image);
        }

        public void bind(TrendCard trend, OnItemClickListener listener) {
            tvTitle.setText(trend.getTitle());
            tvAuthor.setText(trend.getAuthor());
            tvCommentCount.setText(String.valueOf(trend.getCommentCount()));
            tvTime.setText(trend.getTime());

            if (trend.getImageResId() != 0) {
                ivTrendImage.setImageResource(trend.getImageResId());
            } else {
                ivTrendImage.setImageResource(R.drawable.img_car_placeholder);
            }

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(trend));
            }
        }
    }
}
