package com.androidcourse.moyan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.Holder> {

    private final List<String> list;
    private OnDeleteClickListener listener;

    public SearchHistoryAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String text = list.get(position);
        holder.tvText.setText(text);

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageView ivDelete;

        public Holder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_content);
            ivDelete = itemView.findViewById(R.id.iv_delete_single);
        }
    }

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.listener = listener;
    }
}