package com.androidcourse.moyan.network;

import android.text.TextUtils;

import com.androidcourse.moyan.model.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommentNetworkManager {

    private static CommentNetworkManager instance;
    private final Gson gson;

    private CommentNetworkManager() {
        gson = new Gson();
    }

    public static CommentNetworkManager getInstance() {
        if (instance == null) {
            instance = new CommentNetworkManager();
        }
        return instance;
    }

    /**
     * 获取帖子的评论列表
     * @param postId 帖子ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @return 评论列表，失败返回null
     */
    public List<Comment> getComments(int postId, int page, int pageSize) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "get_comments");
            request.put("postId", postId);
            request.put("page", page);
            request.put("pageSize", pageSize);

            String response = SocketClient.getInstance().sendRequest(request.toString());
            if (TextUtils.isEmpty(response)) {
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");

            if (code == 0) {
                JSONObject data = jsonResponse.getJSONObject("data");
                JSONArray commentsArray = data.getJSONArray("comments");

                Type listType = new TypeToken<List<Comment>>() {}.getType();
                return gson.fromJson(commentsArray.toString(), listType);
            } else {
                String msg = jsonResponse.optString("msg", "获取评论失败");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发表评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param replyToCommentId 回复的评论ID（0表示不是回复）
     * @return 是否成功
     */
    public boolean addComment(int postId, int userId, String content, int replyToCommentId) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "add_comment");
            request.put("postId", postId);
            request.put("userId", userId);
            request.put("content", content);
            request.put("replyToCommentId", replyToCommentId);

            String response = SocketClient.getInstance().sendRequest(request.toString());
            if (TextUtils.isEmpty(response)) {
                return false;
            }

            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");

            return code == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 当前用户ID（用于权限验证）
     * @return 是否成功
     */
    public boolean deleteComment(int commentId, int userId) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "delete_comment");
            request.put("commentId", commentId);
            request.put("userId", userId);

            String response = SocketClient.getInstance().sendRequest(request.toString());
            if (TextUtils.isEmpty(response)) {
                return false;
            }

            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");

            return code == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 点赞/取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @param isLiked true=点赞，false=取消点赞
     * @return 是否成功
     */
    public boolean likeComment(int commentId, int userId, boolean isLiked) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "like_comment");
            request.put("commentId", commentId);
            request.put("userId", userId);
            request.put("isLike", isLiked);

            String response = SocketClient.getInstance().sendRequest(request.toString());
            if (TextUtils.isEmpty(response)) {
                return false;
            }

            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");

            return code == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取评论总数
     * @param postId 帖子ID
     * @return 评论总数，失败返回0
     */
    public int getCommentCount(int postId) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "get_comment_count");
            request.put("postId", postId);

            String response = SocketClient.getInstance().sendRequest(request.toString());
            if (TextUtils.isEmpty(response)) {
                return 0;
            }

            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");

            if (code == 0) {
                JSONObject data = jsonResponse.getJSONObject("data");
                return data.getInt("count");
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取评论的回复列表（用于嵌套回复）
     * @param commentId 评论ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 回复列表
     */
    public List<Comment> getReplies(int commentId, int page, int pageSize) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "get_replies");
            request.put("commentId", commentId);
            request.put("page", page);
            request.put("pageSize", pageSize);

            String response = SocketClient.getInstance().sendRequest(request.toString());
            if (TextUtils.isEmpty(response)) {
                return null;
            }

            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");

            if (code == 0) {
                JSONObject data = jsonResponse.getJSONObject("data");
                JSONArray repliesArray = data.getJSONArray("replies");

                Type listType = new TypeToken<List<Comment>>() {}.getType();
                return gson.fromJson(repliesArray.toString(), listType);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}