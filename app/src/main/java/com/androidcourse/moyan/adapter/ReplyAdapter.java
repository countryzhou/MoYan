package com.androidcourse.moyan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.activity.ReportActivity;
import com.androidcourse.moyan.model.Reply;
import com.androidcourse.moyan.utils.TimeUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private List<Reply> replyList;
    private int currentUserId;
    private Context context;
    private OnReplyActionListener listener;

    public interface OnReplyActionListener {
        void onReplyClick(Reply reply);
        void onDeleteClick(Reply reply, int position);
        void onAvatarClick(Reply reply);
    }

    public ReplyAdapter(List<Reply> replyList, int currentUserId) {
        this.replyList = replyList;
        this.currentUserId = currentUserId;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    public void setOnReplyActionListener(OnReplyActionListener listener) {
        this.listener = listener;
    }

    public void updateReplies(List<Reply> newReplies) {
        this.replyList = newReplies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_item_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reply reply = replyList.get(position);
        holder.bind(reply, position);
    }

    @Override
    public int getItemCount() {
        return replyList == null ? 0 : replyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivReplyAvatar;
        private TextView tvReplyNickname;
        private TextView tvReplyContent;
        private TextView tvReplyTime;
        private TextView tvReplyDelete;
        private TextView tvReportReply;  // ✅ 新增：举报按钮

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReplyAvatar = itemView.findViewById(R.id.ivReplyAvatar);
            tvReplyNickname = itemView.findViewById(R.id.tvReplyNickname);
            tvReplyContent = itemView.findViewById(R.id.tvReplyContent);
            tvReplyTime = itemView.findViewById(R.id.tvReplyTime);
            tvReplyDelete = itemView.findViewById(R.id.tvReplyDelete);
            tvReportReply = itemView.findViewById(R.id.tvReportReply);  // ✅ 新增
        }

        public void bind(Reply reply, int position) {
            tvReplyNickname.setText(reply.getDisplayName());
            tvReplyContent.setText(reply.getContent());
            tvReplyTime.setText(TimeUtils.formatRelativeTime(reply.getReplyTime()));

            if (reply.isAnonymous()) {
                ivReplyAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                ivReplyAvatar.setClickable(false);
            } else {
                ivReplyAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                ivReplyAvatar.setClickable(true);
                ivReplyAvatar.setOnClickListener(v -> {
                    if (listener != null && reply.isProfileAccessible()) {
                        listener.onAvatarClick(reply);
                    }
                });
            }

            if (reply.getUserId() == currentUserId) {
                tvReplyDelete.setVisibility(View.VISIBLE);
                tvReplyDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(reply, position);
                    }
                });
            } else {
                tvReplyDelete.setVisibility(View.GONE);
            }

            // ✅ 新增：举报按钮点击事件
            if (tvReportReply != null) {
                tvReportReply.setVisibility(View.VISIBLE);
                tvReportReply.setOnClickListener(v -> {
                    if (context != null) {
                        Intent intent = new Intent(context, ReportActivity.class);
                        intent.putExtra("target_type", 2);  // 2 = 回复
                        intent.putExtra("target_id", reply.getReplyId());
                        context.startActivity(intent);
                    }
                });
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReplyClick(reply);
                }
            });
        }
    }
}