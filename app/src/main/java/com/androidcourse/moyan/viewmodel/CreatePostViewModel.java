package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.androidcourse.moyan.repository.PostRepository;
import com.androidcourse.moyan.utils.AnonymousHelper;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 发帖ViewModel
 * 负责：发布帖子（包括匿名发帖逻辑）
 * 调用：PostRepository + SharedPrefsHelper + AnonymousHelper
 */
public class CreatePostViewModel {

    private PostRepository postRepository;
    private Handler mainHandler;

    public CreatePostViewModel() {
        postRepository = new PostRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 发布帖子
     * @param title 标题     * @param content 内容
     * @param tags 标签
     * @param isAnonymous 是否匿名
     * @param callback 回调
     */
    public void createPost(String title, String content, String tags,
                           boolean isAnonymous, CreatePostCallback callback) {
        // 表单校验
        String error = validatePostForm(title, content);
        if (error != null) {
            if (callback != null) callback.onValidationError(error);
            return;
        }

        int userId = SharedPrefsHelper.getInstance().getUserId();

        postRepository.createPost(userId, isAnonymous, title, content, tags,
                new PostRepository.RepositoryCallback<Integer>() {
                    @Override
                    public void onResult(Integer result) {
                        mainHandler.post(() -> {
                            if (callback != null) callback.onSuccess(result);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        mainHandler.post(() -> {
                            if (callback != null) callback.onFailure(error);
                        });
                    }
                });
    }

    /**
     * 校验发帖表单
     */
    private String validatePostForm(String title, String content) {
        if (TextUtils.isEmpty(title)) {
            return "请输入标题";
        }
        if (TextUtils.isEmpty(content)) {
            return "请输入内容";
        }
        if (title.length() < 2) {
            return "标题至少需要2个字";
        }
        return null;
    }

    /**
     * 当前是否匿名模式
     */
    public boolean isAnonymousMode() {
        return AnonymousHelper.isAnonymousActive();
    }

    /**
     * 切换匿名模式
     */
    public void toggleAnonymous(boolean enable) {
        AnonymousHelper.toggleAnonymous(enable);
    }

    /**
     * 获取当前匿名名称
     */
    public String getAnonymousName() {
        return AnonymousHelper.getCurrentAnonymousName();
    }

    /**
     * 发帖回调接口
     */
    public interface CreatePostCallback {
        void onSuccess(int postId);
        void onFailure(String error);
        void onValidationError(String error);
    }
}