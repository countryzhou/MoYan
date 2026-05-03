package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.model.Comment;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.repository.CommentRepository;
import com.androidcourse.moyan.repository.PostRepository;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

import java.util.List;

/**
 * 帖子详情ViewModel
 * 负责：加载帖子详情、加载评论、发表评论、点赞、收藏
 * 调用：PostRepository + CommentRepository
 */
public class PostDetailViewModel {

    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private Handler mainHandler;

    public PostDetailViewModel() {
        postRepository = new PostRepository();
        commentRepository = new CommentRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 加载帖子详情
     * 对应API序号9
     * @param postId 帖子ID
     * @param isDebug 是否Mock模式
     * @param callback 回调
     */
    public void loadPostDetail(int postId, boolean isDebug, PostDetailCallback callback) {
        if (isDebug) {
            // Mock模式：使用模拟数据
            Post mockPost = getMockPost();
            if (callback != null) callback.onSuccess(mockPost);
            return;
        }

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
     * 加载评论列表
     * @param postId 帖子ID
     * @param callback 回调
     */
    public void loadComments(int postId, CommentListCallback callback) {
        commentRepository.getComments(postId, 1, 50,
                new CommentRepository.RepositoryCallback<List<Comment>>() {
                    @Override
                    public void onResult(List<Comment> result) {
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
     * 发表评论
     * @param postId 帖子ID
     * @param content 评论内容
     * @param callback 回调
     */
    public void submitComment(int postId, String content, SubmitCallback callback) {
        int userId = SharedPrefsHelper.getInstance().getUserId();
        commentRepository.addComment(postId, userId, content, 0,
                new CommentRepository.RepositoryCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
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
     * 发表回复
     * 对应API序号11
     * @param postId 帖子ID
     * @param content 回复内容
     * @param isAnonymous 是否匿名
     * @param callback 回调
     */
    public void submitReply(int postId, String content, boolean isAnonymous,
                            SubmitCallback callback) {
        int userId = SharedPrefsHelper.getInstance().getUserId();
        commentRepository.createReply(postId, userId, isAnonymous, content,
                new CommentRepository.RepositoryCallback<Integer>() {
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
     * 对应API序号12
     * @param commentId 评论ID
     * @param callback 回调
     */
    public void loadReplies(int commentId, CommentListCallback callback) {
        commentRepository.getReplies(commentId, 1, 20,
                new CommentRepository.RepositoryCallback<List<Comment>>() {
                    @Override
                    public void onResult(List<Comment> result) {
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

    // ==================== 模拟数据（仅Mock模式使用） ====================

    /**
     * 模拟帖子数据
     */
    private Post getMockPost() {
        Post post = new Post();
        post.setPostId(1);
        post.setUserId(100);
        post.setTitle("林丹7000万豪宅曝光！内部装修如皇宫");
        post.setContent("北京时间1月10日消息，林丹在老家福建的别墅曝光，这套豪宅位于富人区，均价11万！而从面积来看，在600-800平方米，因此，这套豪宅在7000万人民币左右。当然，豪宅内部装修也相当豪华，就像是皇宫一样。当然，林丹打了20年球，靠打球的收入就超过2亿。因此，7000万对林丹来说不算什么。");
        post.setTags("体育,明星,豪宅");
        post.setAnonymous(false);
        post.setLikeCount(279);
        post.setReplyCount(18);
        post.setViewCount(10000);
        post.setCreateTime(System.currentTimeMillis() - 3600000);
        post.setUpdateTime(System.currentTimeMillis() - 1800000);
        post.setNickname("郭敬明");
        post.setAvatarUrl(null);
        post.setLiked(false);
        return post;
    }

    /**
     * 帖子详情回调接口
     */
    public interface PostDetailCallback {
        void onSuccess(Post post);
        void onFailure(String error);
    }

    /**
     * 评论列表回调接口
     */
    public interface CommentListCallback {
        void onSuccess(List<Comment> comments);
        void onFailure(String error);
    }

    /**
     * 提交操作回调接口
     */
    public interface SubmitCallback {
        void onSuccess();
        void onFailure(String error);
    }
}