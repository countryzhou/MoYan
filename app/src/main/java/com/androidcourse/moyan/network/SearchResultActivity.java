package com.androidcourse.moyan.network;

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
import com.androidcourse.moyan.adapter.SearchResultAdapter;
import com.androidcourse.moyan.adapter.SearchResultAdapter.SearchResult;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private EditText et_search;
    private ImageView iv_clear;
    private RecyclerView rv_search_result;
    private SearchResultAdapter adapter;
    private List<SearchResult> searchResults;

    // 分类标签
    private TextView tv_all, tv_user, tv_product, tv_image;
    // 子分类标签
    private TextView tv_comprehensive, tv_latest, tv_account, tv_photo, tv_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // 初始化控件
        et_search = findViewById(R.id.et_search);
        iv_clear = findViewById(R.id.iv_clear);
        rv_search_result = findViewById(R.id.rv_search_result);

        // 分类标签
        tv_all = findViewById(R.id.tv_all);
        tv_user = findViewById(R.id.tv_user);
        tv_product = findViewById(R.id.tv_product);
        tv_image = findViewById(R.id.tv_image);

        // 子分类标签
        tv_comprehensive = findViewById(R.id.tv_comprehensive);
        tv_latest = findViewById(R.id.tv_latest);
        tv_account = findViewById(R.id.tv_account);
        tv_photo = findViewById(R.id.tv_photo);
        tv_avatar = findViewById(R.id.tv_avatar);

        // 设置返回按钮
        findViewById(R.id.tv_back).setOnClickListener(v -> finish());

        // 设置搜索框
        String searchText = getIntent().getStringExtra("search_text");
        if (!TextUtils.isEmpty(searchText)) {
            et_search.setText(searchText);
            iv_clear.setVisibility(View.VISIBLE);
        }

        // 清除按钮点击事件
        iv_clear.setOnClickListener(v -> {
            et_search.setText("");
            iv_clear.setVisibility(View.GONE);
        });

        // 初始化搜索结果数据
        initSearchResults();

        // 设置适配器
        adapter = new SearchResultAdapter(searchResults);
        rv_search_result.setLayoutManager(new LinearLayoutManager(this));
        rv_search_result.setAdapter(adapter);

        // 设置分类标签点击事件
        setCategoryClickListeners();

        // 设置子分类标签点击事件
        setSubCategoryClickListeners();
    }

    private void initSearchResults() {
        searchResults = new ArrayList<>();
        // 添加模拟数据
        searchResults.add(new SearchResult(
                "QQ少年",
                "小红书号：178738646",
                "今年下半年有正缘\n没有老师\n哈哈\n老师这咋知道的\n我给你用豆包算了算",
                "要多瘦才算瘦啊 #马甲线 #腰围51 #腹肌 #...",
                "01-28",
                "163",
                "4"
        ));
        searchResults.add(new SearchResult(
                "momo",
                "小红书号：123456789",
                "我以为老师要给我介绍对象[捂脸]我以为老...",
                "#恋爱 #脱单 #正缘",
                "04-01",
                "4",
                "1"
        ));
    }

    private void setCategoryClickListeners() {
        View.OnClickListener categoryListener = v -> {
            // 重置所有标签颜色
            resetCategoryTags();
            // 设置当前标签为红色
            ((TextView) v).setTextColor(getResources().getColor(R.color.red));
        };

        tv_all.setOnClickListener(categoryListener);
        tv_user.setOnClickListener(categoryListener);
        tv_product.setOnClickListener(categoryListener);
        tv_image.setOnClickListener(categoryListener);
    }

    private void setSubCategoryClickListeners() {
        View.OnClickListener subCategoryListener = v -> {
            // 重置所有子标签颜色
            resetSubCategoryTags();
            // 设置当前标签为黑色
            ((TextView) v).setTextColor(getResources().getColor(R.color.black));
        };

        tv_comprehensive.setOnClickListener(subCategoryListener);
        tv_latest.setOnClickListener(subCategoryListener);
        tv_account.setOnClickListener(subCategoryListener);
        tv_photo.setOnClickListener(subCategoryListener);
        tv_avatar.setOnClickListener(subCategoryListener);
    }

    private void resetCategoryTags() {
        tv_all.setTextColor(getResources().getColor(R.color.black));
        tv_user.setTextColor(getResources().getColor(R.color.black));
        tv_product.setTextColor(getResources().getColor(R.color.black));
        tv_image.setTextColor(getResources().getColor(R.color.black));
    }

    private void resetSubCategoryTags() {
        tv_comprehensive.setTextColor(getResources().getColor(R.color.gray));
        tv_latest.setTextColor(getResources().getColor(R.color.gray));
        tv_account.setTextColor(getResources().getColor(R.color.gray));
        tv_photo.setTextColor(getResources().getColor(R.color.gray));
        tv_avatar.setTextColor(getResources().getColor(R.color.gray));
    }
}
