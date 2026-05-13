package com.androidcourse.moyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.moyan.R;
import com.androidcourse.moyan.model.Tag;
import com.androidcourse.moyan.utils.ImagePickerHelper;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.viewmodel.CreatePostViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布帖子页面
 */
public class CreatepostActivity extends AppCompatActivity {

    // UI组件
    private ImageView ivBack;
    private EditText etContent;
    private LinearLayout llImageContainer;
    private LinearLayout llAddImage;
    private LinearLayout llAddTag;
    private LinearLayout llSelectedTags;
    private LinearLayout anonymousContainer;
    private CheckBox anonymousCheckBox;
    private TextView tvSend;
    private LinearLayout llDrafts;

    // 图片相关
    private ImagePickerHelper imagePickerHelper;
    private ActivityResultLauncher<String> galleryLauncher;
    private List<String> imagePaths = new ArrayList<>();
    private List<View> imageItemViews = new ArrayList<>();

    // 标签相关
    private List<String> selectedTags = new ArrayList<>();
    private List<Tag> allTags = new ArrayList<>();

    // ViewModel
    private CreatePostViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);

        // 添加登录状态检查
        int userId = SharedPrefsHelper.getInstance().getUserId();
        boolean isLogin = SharedPrefsHelper.getInstance().isLogin();
        android.util.Log.d("CreatePost", "当前用户ID: " + userId);
        android.util.Log.d("CreatePost", "是否登录: " + isLogin);

        initViews();
        initImagePicker();
        setupViewModel();
        setupListeners();
        setupBackHandler(); // 添加返回手势处理

        // 检查是否有草稿ID传入
        int draftId = getIntent().getIntExtra("draft_id", -1);
        if (draftId != -1) {
            viewModel.loadDraft(draftId);
        }
    }

    /**
     * 设置返回手势处理（兼容 Android 14+）
     */
    private void setupBackHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        etContent = findViewById(R.id.etContent);
        llImageContainer = findViewById(R.id.llImageContainer);
        llAddImage = findViewById(R.id.llAddImage);
        llAddTag = findViewById(R.id.llAddTag);
        llSelectedTags = findViewById(R.id.llSelectedTags);
        anonymousContainer = findViewById(R.id.anonymousContainer);
        anonymousCheckBox = findViewById(R.id.anonymousCheckBox);
        tvSend = findViewById(R.id.tvSend);
        llDrafts = findViewById(R.id.llDrafts);
    }

    private void initImagePicker() {
        imagePickerHelper = new ImagePickerHelper(this);

        // 注册图片选择器（多图）
        galleryLauncher = imagePickerHelper.registerMultipleGalleryLauncher();

        // 设置回调
        imagePickerHelper.setCallback(new ImagePickerHelper.ImagePickCallback() {
            @Override
            public void onImagesPicked(List<String> paths) {
                if (paths != null && !paths.isEmpty()) {
                    int remaining = ImagePickerHelper.getMaxImages() - imagePaths.size();
                    if (paths.size() > remaining) {
                        paths = paths.subList(0, remaining);
                        Toast.makeText(CreatepostActivity.this,
                                "最多只能选择" + ImagePickerHelper.getMaxImages() + "张图片",
                                Toast.LENGTH_SHORT).show();
                    }
                    viewModel.addImagePaths(paths);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CreatepostActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CreatePostViewModel.class);

        // 观察内容
        viewModel.getContent().observe(this, content -> {
            if (!TextUtils.equals(etContent.getText().toString(), content)) {
                etContent.setText(content);
            }
        });

        // 观察图片路径
        viewModel.getImagePaths().observe(this, paths -> {
            this.imagePaths.clear();
            this.imagePaths.addAll(paths);
            updateImageDisplay();
        });

        // 观察选中的标签
        viewModel.getSelectedTags().observe(this, tags -> {
            this.selectedTags.clear();
            this.selectedTags.addAll(tags);
            updateSelectedTagsDisplay();
        });

        // 观察匿名状态
        viewModel.getIsAnonymous().observe(this, isAnonymous -> {
            anonymousCheckBox.setChecked(isAnonymous != null && isAnonymous);
        });

        // 观察所有标签
        viewModel.getAllTags().observe(this, tags -> {
            if (tags != null) {
                this.allTags.clear();
                this.allTags.addAll(tags);
            }
        });

        // 观察Toast消息
        viewModel.getToastMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // 观察是否关闭页面
        viewModel.getFinishActivity().observe(this, shouldFinish -> {
            if (shouldFinish) {
                finish();
            }
        });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> handleBackPress());

        tvSend.setOnClickListener(v -> viewModel.publishPost());

        llDrafts.setOnClickListener(v -> {
            // TODO: 跳转到草稿箱页面（如果存在的话）
            Toast.makeText(this, "草稿箱功能开发中", Toast.LENGTH_SHORT).show();
        });

        llAddImage.setOnClickListener(v -> {
            if (imagePaths.size() >= ImagePickerHelper.getMaxImages()) {
                Toast.makeText(this,
                        "最多只能选择" + ImagePickerHelper.getMaxImages() + "张图片",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            galleryLauncher.launch("image/*");
        });

        llAddTag.setOnClickListener(v -> showTagSelectionDialog());

        anonymousContainer.setOnClickListener(v -> {
            anonymousCheckBox.toggle();
            viewModel.setAnonymous(anonymousCheckBox.isChecked());
        });

        // 内容变化时同步到ViewModel
        etContent.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                viewModel.setContent(s.toString());
            }
        });
    }

    /**
     * 处理返回事件（统一处理）
     */
    private void handleBackPress() {
        String content = etContent.getText().toString().trim();
        boolean hasContent = !TextUtils.isEmpty(content);
        boolean hasImages = !imagePaths.isEmpty();
        boolean hasTags = !selectedTags.isEmpty();

        if (hasContent || hasImages || hasTags) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("是否保存草稿？")
                    .setPositiveButton("保存", (dialog, which) -> {
                        viewModel.saveDraft();
                        finish();
                    })
                    .setNegativeButton("不保存", (dialog, which) -> {
                        finish();
                    })
                    .setNeutralButton("取消", null)
                    .show();
        } else {
            finish();
        }
    }

    /**
     * 更新图片显示（使用 item_image_preview.xml 布局）
     */
    private void updateImageDisplay() {
        llImageContainer.removeAllViews();
        imageItemViews.clear();

        if (imagePaths.isEmpty()) {
            return;
        }

        for (int i = 0; i < imagePaths.size(); i++) {
            String path = imagePaths.get(i);
            View imageItemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_image_preview, llImageContainer, false);

            ImageView ivPreview = imageItemView.findViewById(R.id.iv_preview);
            ImageView ivDelete = imageItemView.findViewById(R.id.iv_delete);
            TextView tvCover = imageItemView.findViewById(R.id.tv_cover);

            // 第一张图片设置为封面
            if (i == 0) {
                tvCover.setVisibility(View.VISIBLE);
            } else {
                tvCover.setVisibility(View.GONE);
            }

            // 加载图片（使用 Android 默认图标作为占位符）
            Glide.with(this)
                    .load(path)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(ivPreview);

            final int position = i;
            ivDelete.setOnClickListener(v -> viewModel.removeImageAt(position));

            // 点击预览大图
            ivPreview.setOnClickListener(v -> {
                Toast.makeText(this, "预览图片 " + (position + 1), Toast.LENGTH_SHORT).show();
            });

            llImageContainer.addView(imageItemView);
            imageItemViews.add(imageItemView);
        }
    }

    /**
     * 更新已选标签显示
     */
    private void updateSelectedTagsDisplay() {
        llSelectedTags.removeAllViews();

        if (selectedTags.isEmpty()) {
            return;
        }

        for (String tag : selectedTags) {
            TextView tagView = (TextView) LayoutInflater.from(this)
                    .inflate(R.layout.item_selected_tag, llSelectedTags, false);
            tagView.setText("#" + tag);
            tagView.setOnClickListener(v -> {
                viewModel.toggleTag(tag);
            });
            llSelectedTags.addView(tagView);
        }
    }

    /**
     * 显示标签选择对话框（使用 dialog_tag_selector.xml）
     */
    private void showTagSelectionDialog() {
        // 使用 Android 默认主题
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tag_selector, null);
        dialog.setContentView(dialogView);

        // 设置对话框宽度为全屏
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 初始化控件
        ImageView ivClose = dialogView.findViewById(R.id.ivClose);
        LinearLayout llTagContainer = dialogView.findViewById(R.id.llTagContainer);
        LinearLayout llCustomInput = dialogView.findViewById(R.id.llCustomInput);
        EditText etCustomTag = dialogView.findViewById(R.id.etCustomTag);
        TextView btnCancelCustom = dialogView.findViewById(R.id.btnCancelCustom);
        TextView btnConfirmCustom = dialogView.findViewById(R.id.btnConfirmCustom);
        TextView btnAddCustom = dialogView.findViewById(R.id.btnAddCustom);
        TextView btnConfirmTags = dialogView.findViewById(R.id.btnConfirmTags);

        // 动态添加标签
        updateTagContainer(llTagContainer, dialog);

        // 关闭按钮
        ivClose.setOnClickListener(v -> dialog.dismiss());

        // 确定按钮
        btnConfirmTags.setOnClickListener(v -> dialog.dismiss());

        // 自定义标签按钮
        btnAddCustom.setOnClickListener(v -> {
            llCustomInput.setVisibility(View.VISIBLE);
            btnAddCustom.setVisibility(View.GONE);
        });

        // 取消自定义标签
        btnCancelCustom.setOnClickListener(v -> {
            llCustomInput.setVisibility(View.GONE);
            btnAddCustom.setVisibility(View.VISIBLE);
            etCustomTag.setText("");
        });

        // 确认添加自定义标签
        btnConfirmCustom.setOnClickListener(v -> {
            String customTag = etCustomTag.getText().toString().trim();
            if (!TextUtils.isEmpty(customTag)) {
                viewModel.addCustomTag(customTag);
                etCustomTag.setText("");
                llCustomInput.setVisibility(View.GONE);
                btnAddCustom.setVisibility(View.VISIBLE);
                // 刷新标签列表
                viewModel.getAllTags().observe(this, tags -> {
                    if (tags != null) {
                        updateTagContainer(llTagContainer, dialog);
                    }
                });
            } else {
                Toast.makeText(this, "请输入标签名称", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    /**
     * 更新标签容器
     */
    private void updateTagContainer(LinearLayout llTagContainer, android.app.Dialog dialog) {
        llTagContainer.removeAllViews();

        // 按类型分组：系统标签在前，自定义标签在后
        List<Tag> systemTags = new ArrayList<>();
        List<Tag> customTags = new ArrayList<>();

        for (Tag tag : allTags) {
            if (tag.isCustom()) {
                customTags.add(tag);
            } else {
                systemTags.add(tag);
            }
        }

        // 添加系统标签标题
        if (!systemTags.isEmpty()) {
            TextView titleView = new TextView(this);
            titleView.setText("推荐标签");
            titleView.setTextSize(14);
            titleView.setTextColor(getColor(android.R.color.darker_gray));
            titleView.setPadding(0, 16, 0, 8);
            llTagContainer.addView(titleView);

            // 添加系统标签
            addTagViews(llTagContainer, systemTags, dialog);
        }

        // 添加自定义标签标题
        if (!customTags.isEmpty()) {
            TextView titleView = new TextView(this);
            titleView.setText("我的标签");
            titleView.setTextSize(14);
            titleView.setTextColor(getColor(android.R.color.darker_gray));
            titleView.setPadding(0, 16, 0, 8);
            llTagContainer.addView(titleView);

            // 添加自定义标签
            addTagViews(llTagContainer, customTags, dialog);
        }
    }

    /**
     * 添加标签视图
     */
    private void addTagViews(LinearLayout container, List<Tag> tags, android.app.Dialog dialog) {
        // 使用流式布局
        LinearLayout rowLayout = null;
        int maxItemsPerRow = 4;

        for (int i = 0; i < tags.size(); i++) {
            if (i % maxItemsPerRow == 0) {
                rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                container.addView(rowLayout);
            }

            Tag tag = tags.get(i);
            TextView tagView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            params.setMargins(0, 0, 8, 8);
            tagView.setLayoutParams(params);
            tagView.setText("#" + tag.getName());
            tagView.setTextSize(12);
            tagView.setPadding(16, 8, 16, 8);

            // 设置背景和颜色
            if (selectedTags.contains(tag.getName())) {
                tagView.setBackgroundResource(R.drawable.bg_tag_selected);
                tagView.setTextColor(getColor(android.R.color.white));
            } else {
                tagView.setBackgroundResource(R.drawable.bg_tag_default);
                tagView.setTextColor(getColor(android.R.color.black));
            }

            // 长按删除自定义标签
            if (tag.isCustom()) {
                tagView.setOnLongClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("删除标签")
                            .setMessage("确定要删除 \"" + tag.getName() + "\" 吗？")
                            .setPositiveButton("确定", (dialogInterface, which) -> {
                                viewModel.deleteCustomTag(tag);
                                dialog.dismiss();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                    return true;
                });
            }

            // 点击选择/取消标签
            tagView.setOnClickListener(v -> {
                viewModel.toggleTag(tag.getName());
                dialog.dismiss();
                showTagSelectionDialog(); // 刷新对话框
            });

            rowLayout.addView(tagView);
        }
    }
}