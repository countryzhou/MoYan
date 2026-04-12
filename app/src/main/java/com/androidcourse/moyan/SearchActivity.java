package com.androidcourse.moyan;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    // 搜索控件
    private EditText etSearch;
    private ImageView ivClear;
    private TextView tvSearch;

    // 历史记录
    private RecyclerView rvHistory;
    private TextView tvExpand, tvClearAll;
    private SearchHistoryAdapter adapter;
    private List<String> allHistoryList = new ArrayList<>();
    private List<String> showHistoryList = new ArrayList<>();
    private boolean isExpanded = false;
    private static final int MAX_SHOW = 3;

    // 标签栏
    private TextView tvAll, tvUser, tvGoods, tvImage, tvAsk;

    // 用户结果
    private View layoutUserResult;
    private TextView tvNickname, tvFans, tvUserId, tvFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initHistoryData();
        initListener();
    }

    private void initView() {
        // 搜索
        etSearch = findViewById(R.id.et_search);
        ivClear = findViewById(R.id.iv_clear);
        tvSearch = findViewById(R.id.tv_search);

        // 历史
        rvHistory = findViewById(R.id.rv_history);
        tvExpand = findViewById(R.id.tv_expand);
        tvClearAll = findViewById(R.id.tv_clear_all);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        // 标签
        tvAll = findViewById(R.id.tv_all);
        tvUser = findViewById(R.id.tv_user);
        tvGoods = findViewById(R.id.tv_goods);
        tvImage = findViewById(R.id.tv_image);
        tvAsk = findViewById(R.id.tv_ask);

        // 用户结果
        layoutUserResult = findViewById(R.id.layout_user_result);
        tvNickname = findViewById(R.id.tv_nickname);
        tvFans = findViewById(R.id.tv_fans);
        tvUserId = findViewById(R.id.tv_user_id);
        tvFollow = findViewById(R.id.tv_follow);
    }

    private void initHistoryData() {
        allHistoryList.clear();
        allHistoryList.add("高级配色色卡大全");
        allHistoryList.add("sqlserver导入excel数据");
        allHistoryList.add("计算机组成原理知识点");
        allHistoryList.add("Android Studio开发教程");
        allHistoryList.add("Java基础语法");
        refreshHistoryList();
    }

    private void refreshHistoryList() {
        showHistoryList.clear();
        if (isExpanded) {
            showHistoryList.addAll(allHistoryList);
            tvExpand.setText("收起");
        } else {
            int count = Math.min(allHistoryList.size(), MAX_SHOW);
            for (int i = 0; i < count; i++) {
                showHistoryList.add(allHistoryList.get(i));
            }
            tvExpand.setText("展开");
        }

        if (adapter == null) {
            adapter = new SearchHistoryAdapter(this, showHistoryList, position -> {
                allHistoryList.remove(showHistoryList.get(position));
                refreshHistoryList();
            });
            rvHistory.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initListener() {
        // 清空输入
        ivClear.setOnClickListener(v -> etSearch.setText(""));

        // 搜索
        tvSearch.setOnClickListener(v -> {
            String key = etSearch.getText().toString().trim();
            if (!key.isEmpty()) {
                allHistoryList.add(0, key);
                refreshHistoryList();
                etSearch.setText("");
                showUserResult(); // 搜索后显示用户
            }
        });

        // 展开/收起
        tvExpand.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            refreshHistoryList();
        });

        // 清空全部历史
        tvClearAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("确认清空")
                    .setMessage("确定要删除所有搜索历史吗？")
                    .setPositiveButton("确定", (d, w) -> {
                        allHistoryList.clear();
                        refreshHistoryList();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // 标签切换
        View.OnClickListener tabClick = v -> {
            resetTab();
            ((TextView) v).setTextColor(0xFF000000);
            ((TextView) v).setTextSize(16);
            ((TextView) v).setTypeface(null, 1);
        };
        tvAll.setOnClickListener(tabClick);
        tvUser.setOnClickListener(tabClick);
        tvGoods.setOnClickListener(tabClick);
        tvImage.setOnClickListener(tabClick);
        tvAsk.setOnClickListener(tabClick);

        // 关注
        tvFollow.setOnClickListener(v -> {
            tvFollow.setText("已关注");
            tvFollow.setBackgroundColor(0xFFCCCCCC);
        });

        // 输入监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void resetTab() {
        tvAll.setTextColor(0xFF999999);
        tvUser.setTextColor(0xFF999999);
        tvGoods.setTextColor(0xFF999999);
        tvImage.setTextColor(0xFF999999);
        tvAsk.setTextColor(0xFF999999);
    }

    private void showUserResult() {
        layoutUserResult.setVisibility(View.VISIBLE);
        tvNickname.setText("QQ少年");
        tvFans.setText("粉丝 7");
        tvUserId.setText("小红书号：178738646");
    }

    // 适配器
    public static class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.Holder> {
        private final Context context;
        private final List<String> list;
        private final OnDeleteListener listener;

        public SearchHistoryAdapter(Context c, List<String> l, OnDeleteListener li) {
            context = c;
            list = l;
            listener = li;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.tvContent.setText(list.get(position));
            holder.ivDelete.setOnClickListener(v -> listener.onDelete(position));
        }

        @Override
        public int getItemCount() { return list.size(); }

        public static class Holder extends RecyclerView.ViewHolder {
            TextView tvContent;
            ImageView ivDelete;

            public Holder(@NonNull View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tv_history_content);
                ivDelete = itemView.findViewById(R.id.iv_delete);
            }
        }

        public interface OnDeleteListener { void onDelete(int position); }
    }
}