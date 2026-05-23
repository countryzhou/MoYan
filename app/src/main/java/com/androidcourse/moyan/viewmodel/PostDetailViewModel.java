package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.model.Reply;
import com.androidcourse.moyan.repository.PostRepository;
import com.androidcourse.moyan.repository.ReplyRepository;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

import java.util.List;

public class PostDetailViewModel {

    private PostRepository postRepository;
    private ReplyRepository replyRepository;
    private Handler mainHandler;

    public PostDetailViewModel() {
        postRepository = new PostRepository();
        replyRepository = new ReplyRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 加载帖子详情
     */
    public void loadPostDetail(int postId, PostDetailCallback callback) {
        postRepository.getPostDetail(postId, new PostRepository.RepositoryCallback<Post>() {
            @Override
            public void onResult(Post result) {
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
     * 发表回复
     */
    public void submitReply(int postId, String content, boolean isAnonymous, SubmitCallback callback) {
        int userId = SharedPrefsHelper.getInstance().getUserId();
        replyRepository.createReply(postId, userId, isAnonymous, content,
                new ReplyRepository.RepositoryCallback<Integer>() {
                    @Override
                    public void onResult(Integer result) {
                        mainHandler.post(() -> {
                            if (callback != null) callback.onSuccess();
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
     * 获取回复列表
     */
    public void loadReplies(int postId, ReplyListCallback callback) {
        replyRepository.getReplies(postId, 1,
                new ReplyRepository.RepositoryCallback<List<Reply>>() {
                    @Override
                    public void onResult(List<Reply> result) {
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

    // ==================== 回调接口 ====================

    public interface PostDetailCallback {
        void onSuccess(Post post);
        void onFailure(String error);
    }

    public interface ReplyListCallback {
        void onSuccess(List<Reply> replies);
        void onFailure(String error);
    }

    public interface SubmitCallback {
        void onSuccess();
        void onFailure(String error);
    }
}