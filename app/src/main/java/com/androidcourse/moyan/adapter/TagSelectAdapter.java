package com.androidcourse.moyan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagSelectAdapter extends RecyclerView.Adapter<TagSelectAdapter.ViewHolder> {

    private List<Tag> tags = new ArrayList<>();
    private Set<String> selectedTagNames = new HashSet<>();
    private OnTagActionListener listener;

    public interface OnTagActionListener {
        void onTagClick(Tag tag, boolean isSelected);
        void onDeleteClick(Tag tag);
    }

    public void setOnTagActionListener(OnTagActionListener listener) {
        this.listener = listener;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedTagNames(Set<String> selectedTagNames) {
        this.selectedTagNames = selectedTagNames != null ? selectedTagNames : new HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = tags.get(position);
        boolean isSelected = selectedTagNames.contains(tag.getName());

        holder.tvTagName.setText(tag.getName());

        // 设置背景样式
        if (isSelected) {
            holder.tvTagName.setBackgroundResource(R.drawable.bg_tag_selected);
            holder.tvTagName.setTextColor(0xFFFFFFFF);
        } else {
            holder.tvTagName.setBackgroundResource(R.drawable.bg_tag_default);
            holder.tvTagName.setTextColor(0xFF383838);
        }

        // 显示删除按钮（仅自定义标签）
        if (tag.isCustom()) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(tag);
                }
            });
        } else {
            holder.ivDelete.setVisibility(View.GONE);
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTagClick(tag, !isSelected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    /**
     * ViewHolder 内部类
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagName;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tvTagName);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}