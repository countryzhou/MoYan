package com.androidcourse.moyan.network;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.androidcourse.moyan.R;
import com.androidcourse.moyan.SearchHistoryAdapter;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText et_search;
    private ImageView iv_clear_all;
    private RecyclerView rv_history;
    private SearchHistoryAdapter adapter;
    private List<String> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViewById(R.id.tv_back).setOnClickListener(v -> finish());

        et_search = findViewById(R.id.et_search);
        iv_clear_all = findViewById(R.id.iv_clear_all);
        rv_history = findViewById(R.id.rv_history);

        TextView tv_search = findViewById(R.id.tv_search);

        historyList = new ArrayList<>();
        historyList.add("测试历史1");
        historyList.add("测试历史2");

        adapter = new SearchHistoryAdapter(historyList);
        rv_history.setLayoutManager(new LinearLayoutManager(this));
        rv_history.setAdapter(adapter);

        tv_search.setOnClickListener(v -> {
            String text = et_search.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                historyList.add(0, text);
                adapter.notifyItemInserted(0);
                // 跳转到搜索结果页面
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                intent.putExtra("search_text", text);
                startActivity(intent);
                et_search.setText("");
            }
        });

        iv_clear_all.setOnClickListener(v -> {
            historyList.clear();
            adapter.notifyDataSetChanged();
        });

        adapter.setOnDeleteClickListener(position -> {
            historyList.remove(position);
            adapter.notifyItemRemoved(position);
        });
    }
}