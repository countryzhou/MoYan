package com.androidcourse.moyan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.androidcourse.moyan.R;
import java.util.ArrayList;
import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {

    private List<String> imagePaths = new ArrayList<>();
    private OnImageActionListener listener;

    public interface OnImageActionListener {
        void onDeleteClick(int position);
        void onCoverClick(int position);
    }

    public void setOnImageActionListener(OnImageActionListener listener) {
        this.listener = listener;
    }

    public void setImagePaths(List<String> paths) {
        this.imagePaths = paths != null ? paths : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = imagePaths.get(position);

        Glide.with(holder.itemView.getContext())
                .load(path)
                .centerCrop()
                .into(holder.ivImage);

        // 封面标识（第一张图片显示"封面"）
        if (position == 0) {
            holder.tvCover.setVisibility(View.VISIBLE);
            holder.tvCover.setText("封面");
        } else {
            holder.tvCover.setVisibility(View.GONE);
        }

        // 删除按钮
        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });

        // 点击设置封面
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != 0) {
                listener.onCoverClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageView ivDelete;
        TextView tvCover;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_preview);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            tvCover = itemView.findViewById(R.id.tv_cover);
        }
    }
}