package com.androidcourse.moyan.repository;

import com.androidcourse.moyan.model.Comment;
import com.androidcourse.moyan.model.Reply;
import com.androidcourse.moyan.network.CommentNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 评论/回复数据仓库
 * 负责：评论列表、发表评论、删除评论、点赞评论、回复列表、发表回复
 * 调用：CommentNetworkManager + SharedPrefsHelper
 */
public class CommentRepository {

    private CommentNetworkManager networkManager;
    private SharedPrefsHelper spHelper;
    private Gson gson;

    public CommentRepository() {
        networkManager = CommentNetworkManager.getInstance();
        spHelper = SharedPrefsHelper.getInstance();
        gson = new Gson();
    }

    /**
     * 获取评论列表
     */
    public void getComments(int postId, int page, int pageSize,
                            RepositoryCallback<List<Comment>> callback) {
        new Thread(() -> {
            String response = networkManager.getComments(postId, page, pageSize);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    JSONArray commentsArray = data.getJSONArray("comments");
                    Type listType = new TypeToken<List<Comment>>() {}.getType();
                    List<Comment> commentList = gson.fromJson(commentsArray.toString(), listType);
                    if (callback != null) {
                        callback.onResult(commentList);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取评论失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析评论列表失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 发表评论
     */
    public void addComment(int postId, int userId, String content, int replyToCommentId,
                           RepositoryCallback<Boolean> callback) {
        new Thread(() -> {
            String response = networkManager.addComment(postId, userId, content, replyToCommentId);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    if (callback != null) {
                        callback.onResult(true);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "发表评论失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析评论响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 删除评论
     */
    public void deleteComment(int commentId, int userId,
                              RepositoryCallback<Boolean> callback) {
        new Thread(() -> {
            String response = networkManager.deleteComment(commentId, userId);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    if (callback != null) {
                        callback.onResult(true);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "删除评论失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析删除评论响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 点赞/取消点赞评论
     */
    public void likeComment(int commentId, int userId, boolean isLike,
                            RepositoryCallback<Boolean> callback) {
        new Thread(() -> {
            String response = networkManager.likeComment(commentId, userId, isLike);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    if (callback != null) {
                        callback.onResult(true);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "操作失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析点赞响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 获取回复列表
     * 对应API序号12
     */
    public void getReplies(int commentId, int page, int pageSize,
                           RepositoryCallback<List<Comment>> callback) {
        new Thread(() -> {
            String response = networkManager.getReplies(commentId, page, pageSize);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    JSONArray repliesArray = data.getJSONArray("replies");
                    Type listType = new TypeToken<List<Comment>>() {}.getType();
                    List<Comment> replyList = gson.fromJson(repliesArray.toString(), listType);
                    if (callback != null) {
                        callback.onResult(replyList);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取回复失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析回复列表失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 发布回复
     * 对应API序号11
     */
    public void createReply(int postId, int userId, boolean isAnonymous, String content,
                            RepositoryCallback<Integer> callback) {
        new Thread(() -> {
            String response = networkManager.createReply(postId, userId, isAnonymous, content);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    int replyId = jsonResponse.getInt("data");
                    if (callback != null) {
                        callback.onResult(replyId);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "发表回复失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析回复响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 获取评论总数
     */
    public void getCommentCount(int postId, RepositoryCallback<Integer> callback) {
        new Thread(() -> {
            String response = networkManager.getCommentCount(postId);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    int count = data.getInt("count");
                    if (callback != null) {
                        callback.onResult(count);
                    }
                } else {
                    if (callback != null) {
                        callback.onError("获取评论数失败");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析评论数响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 通用回调接口
     */
    public interface RepositoryCallback<T> {
        void onResult(T result);
        void onError(String error);
    }
}