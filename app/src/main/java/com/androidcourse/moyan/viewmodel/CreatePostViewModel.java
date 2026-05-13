package com.androidcourse.moyan.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.json.JSONObject;

import com.androidcourse.moyan.model.Draft;
import com.androidcourse.moyan.model.Tag;
import com.androidcourse.moyan.network.PostNetworkManager;
import com.androidcourse.moyan.repository.DraftRepository;
import com.androidcourse.moyan.repository.TagRepository;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

import java.util.ArrayList;
import java.util.List;

public class CreatePostViewModel extends AndroidViewModel {

    private final DraftRepository draftRepository;
    private final TagRepository tagRepository;

    // LiveData
    private final MutableLiveData<String> content = new MutableLiveData<>("");
    private final MutableLiveData<List<String>> imagePaths = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<String>> selectedTags = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isAnonymous = new MutableLiveData<>(false);
    private final MutableLiveData<List<Tag>> allTags = new MutableLiveData<>(new ArrayList<>());

    // UI状态
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> finishActivity = new MutableLiveData<>(false);

    private int currentDraftId = -1; // -1表示新建草稿

    public CreatePostViewModel(Application application) {
        super(application);
        draftRepository = new DraftRepository(application);
        tagRepository = new TagRepository(application);
        loadAllTags();
    }

    // 添加 getUserId 方法
    private int getUserId() {
        return SharedPrefsHelper.getInstance().getUserId();
    }

    // Getters
    public LiveData<String> getContent() { return content; }
    public LiveData<List<String>> getImagePaths() { return imagePaths; }
    public LiveData<List<String>> getSelectedTags() { return selectedTags; }
    public LiveData<Boolean> getIsAnonymous() { return isAnonymous; }
    public LiveData<List<Tag>> getAllTags() { return allTags; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<Boolean> getFinishActivity() { return finishActivity; }

    // Setters
    public void setContent(String text) { content.setValue(text); }
    public void setAnonymous(boolean anonymous) { isAnonymous.setValue(anonymous); }

    // 添加/移除图片
    public void addImagePaths(List<String> paths) {
        List<String> current = imagePaths.getValue();
        if (current == null) current = new ArrayList<>();
        current.addAll(paths);
        imagePaths.setValue(current);
    }

    public void removeImageAt(int position) {
        List<String> current = imagePaths.getValue();
        if (current != null && position < current.size()) {
            current.remove(position);
            imagePaths.setValue(current);
        }
    }

    public void clearImages() {
        imagePaths.setValue(new ArrayList<>());
    }

    // 标签操作
    public void toggleTag(String tagName) {
        List<String> current = selectedTags.getValue();
        if (current == null) current = new ArrayList<>();

        if (current.contains(tagName)) {
            current.remove(tagName);
        } else if (current.size() < 4) { // 最多选4个标签
            current.add(tagName);
        } else {
            toastMessage.setValue("最多只能选择4个标签");
            return;
        }
        selectedTags.setValue(current);
    }

    public boolean isTagSelected(String tagName) {
        List<String> current = selectedTags.getValue();
        return current != null && current.contains(tagName);
    }

    // 加载所有标签
    private void loadAllTags() {
        tagRepository.getAllTags(new TagRepository.TagCallback() {
            @Override
            public void onSuccess(List<Tag> tags) {
                allTags.setValue(tags);
            }

            @Override
            public void onError(String error) {
                toastMessage.setValue("加载标签失败: " + error);
            }
        });
    }

    // 添加自定义标签
    public void addCustomTag(String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            toastMessage.setValue("标签名称不能为空");
            return;
        }
        if (tagName.length() > 12) {
            toastMessage.setValue("标签名称不能超过12个字");
            return;
        }

        tagRepository.addTag(tagName, true, new TagRepository.InsertCallback() {
            @Override
            public void onSuccess(long id) {
                loadAllTags(); // 重新加载标签列表
                toastMessage.setValue("添加标签成功");
            }

            @Override
            public void onError(String error) {
                toastMessage.setValue("添加标签失败: " + error);
            }
        });
    }

    // 删除自定义标签
    public void deleteCustomTag(Tag tag) {
        if (!tag.isCustom()) {
            toastMessage.setValue("不能删除系统标签");
            return;
        }

        tagRepository.deleteTag(tag,
                () -> {
                    loadAllTags();
                    // 如果当前选中的标签包含被删除的标签，也移除
                    List<String> current = selectedTags.getValue();
                    if (current != null && current.contains(tag.getName())) {
                        current.remove(tag.getName());
                        selectedTags.setValue(current);
                    }
                    toastMessage.setValue("删除标签成功");
                },
                () -> toastMessage.setValue("删除标签失败")
        );
    }

    // 保存草稿
    public void saveDraft() {
        isLoading.setValue(true);

        Draft draft = new Draft();
        draft.setContent(content.getValue() != null ? content.getValue() : "");
        draft.setImagePaths(imagePaths.getValue() != null ? imagePaths.getValue() : new ArrayList<>());
        draft.setTags(selectedTags.getValue() != null ? selectedTags.getValue() : new ArrayList<>());
        draft.setAnonymous(isAnonymous.getValue() != null && isAnonymous.getValue());

        if (currentDraftId > 0) {
            draft.setId(currentDraftId);
            draftRepository.updateDraft(draft, new DraftRepository.InsertCallback() {
                @Override
                public void onSuccess(long id) {
                    isLoading.setValue(false);
                    toastMessage.setValue("草稿已保存");
                }

                @Override
                public void onError(String error) {
                    isLoading.setValue(false);
                    toastMessage.setValue("保存草稿失败: " + error);
                }
            });
        } else {
            draftRepository.saveDraft(draft, new DraftRepository.InsertCallback() {
                @Override
                public void onSuccess(long id) {
                    currentDraftId = (int) id;
                    isLoading.setValue(false);
                    toastMessage.setValue("草稿已保存");
                }

                @Override
                public void onError(String error) {
                    isLoading.setValue(false);
                    toastMessage.setValue("保存草稿失败: " + error);
                }
            });
        }
    }

    // 加载草稿
    public void loadDraft(int draftId) {
        isLoading.setValue(true);
        draftRepository.getDraftById(draftId, new DraftRepository.SingleDraftCallback() {
            @Override
            public void onSuccess(Draft draft) {
                currentDraftId = draft.getId();
                content.setValue(draft.getContent() != null ? draft.getContent() : "");
                imagePaths.setValue(draft.getImagePaths() != null ? draft.getImagePaths() : new ArrayList<>());
                selectedTags.setValue(draft.getTags() != null ? draft.getTags() : new ArrayList<>());
                isAnonymous.setValue(draft.isAnonymous());
                isLoading.setValue(false);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                toastMessage.setValue("加载草稿失败: " + error);
            }
        });
    }

    // 发布帖子
    // 发布帖子
    public void publishPost() {
        String postContent = content.getValue();
        if (postContent == null || postContent.trim().isEmpty()) {
            toastMessage.setValue("请输入内容");
            return;
        }

        isLoading.setValue(true);

        // 获取数据
        List<String> images = imagePaths.getValue();
        List<String> tags = selectedTags.getValue();
        boolean anonymous = isAnonymous.getValue() != null && isAnonymous.getValue();
        String tagsStr = tags != null && !tags.isEmpty() ?
                String.join(",", tags) : "";

        // 设置标题（使用内容前50字）
        String title = postContent.length() > 50 ? postContent.substring(0, 50) : postContent;

        // 获取用户ID
        int userId = getUserId();
        if (userId <= 0) {
            isLoading.setValue(false);
            toastMessage.setValue("请先登录");
            return;
        }

        // 添加日志
        android.util.Log.d("CreatePostVM", "=== 发帖信息 ===");
        android.util.Log.d("CreatePostVM", "用户ID: " + userId);
        android.util.Log.d("CreatePostVM", "是否匿名: " + anonymous);
        android.util.Log.d("CreatePostVM", "标题: " + title);
        android.util.Log.d("CreatePostVM", "内容: " + postContent);
        android.util.Log.d("CreatePostVM", "标签: " + tagsStr);
        android.util.Log.d("CreatePostVM", "图片数量: " + (images != null ? images.size() : 0));

        // 直接使用 PostNetworkManager 发送请求
        new Thread(() -> {
            try {
                String response = PostNetworkManager.getInstance()
                        .createPost(userId, anonymous, title, postContent, tagsStr, images);

                android.util.Log.d("CreatePostVM", "服务器响应: " + response);

                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    int postId = jsonResponse.getInt("data");
                    android.util.Log.d("CreatePostVM", "发布成功，帖子ID: " + postId);

                    isLoading.postValue(false);
                    toastMessage.postValue("发布成功");
                    finishActivity.postValue(true);
                } else {
                    String errorMsg = jsonResponse.optString("msg", "发布失败");
                    android.util.Log.e("CreatePostVM", "发布失败: " + errorMsg);

                    isLoading.postValue(false);
                    toastMessage.postValue("发布失败: " + errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                android.util.Log.e("CreatePostVM", "发布异常: " + e.getMessage());

                isLoading.postValue(false);
                toastMessage.postValue("发布失败: " + e.getMessage());
            }
        }).start();
    }
}