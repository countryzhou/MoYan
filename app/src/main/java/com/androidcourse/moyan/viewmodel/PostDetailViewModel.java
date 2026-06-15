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
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;

    public PostDetailViewModel() {
        postRepository = new PostRepository();
        replyRepository = new ReplyRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

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

    public void loadReplies(int postId, int page, ReplyListCallback callback) {
        replyRepository.getReplies(postId, page, new ReplyRepository.RepositoryCallback<List<Reply>>() {
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

    public void loadMoreReplies(int postId, ReplyListCallback callback) {
        currentPage++;
        loadReplies(postId, currentPage, callback);
    }

    public void resetPagination() {
        currentPage = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

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
