package com.androidcourse.moyan.network;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * 评论/回复相关网络请求管理器
 * 负责：获取评论列表、发表评论、删除评论、点赞评论、获取回复列表
 */
public class CommentNetworkManager {

    private static CommentNetworkManager instance;

    private CommentNetworkManager() {}

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
     * @return 服务端响应JSON字符串
     */
    public String getComments(int postId, int page, int pageSize) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "get_comments");
            request.put("postId", postId);
            request.put("page", page);
            request.put("pageSize", pageSize);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 发表评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param replyToCommentId 回复的评论ID（0表示直接评论帖子）
     * @return 服务端响应JSON字符串
     */
    public String addComment(int postId, int userId, String content, int replyToCommentId) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "add_comment");
            request.put("postId", postId);
            request.put("userId", userId);
            request.put("content", content);
            request.put("replyToCommentId", replyToCommentId);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 当前用户ID（用于权限验证）
     * @return 服务端响应JSON字符串
     */
    public String deleteComment(int commentId, int userId) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "delete_comment");
            request.put("commentId", commentId);
            request.put("userId", userId);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 点赞/取消点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @param isLike true=点赞，false=取消点赞
     * @return 服务端响应JSON字符串
     */
    public String likeComment(int commentId, int userId, boolean isLike) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "like_comment");
            request.put("commentId", commentId);
            request.put("userId", userId);
            request.put("isLike", isLike);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取评论的回复列表（用于嵌套回复）
     * @param commentId 评论ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 服务端响应JSON字符串
     */
    public String getReplies(int commentId, int page, int pageSize) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "get_replies");
            request.put("commentId", commentId);
            request.put("page", page);
            request.put("pageSize", pageSize);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取评论总数
     * @param postId 帖子ID
     * @return 服务端响应JSON字符串
     */
    public String getCommentCount(int postId) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "get_comment_count");
            request.put("postId", postId);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 发布回复（对应API序号11）
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param isAnonymous 是否匿名
     * @param content 回复内容
     * @return 服务端响应JSON字符串
     */
    public String createReply(int postId, int userId, boolean isAnonymous, String content) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "createReply");

            JSONObject params = new JSONObject();
            params.put("postId", postId);
            params.put("userId", userId);
            params.put("isAnonymous", isAnonymous);
            params.put("content", content);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取回复列表（对应API序号12）
     * @param postId 帖子ID
     * @param page 页码
     * @return 服务端响应JSON字符串
     */
    public String getReplyList(int postId, int page) {
        try {
            JSONObject request = new JSONObject();
            request.put("type", "getReplies");

            JSONObject params = new JSONObject();
            params.put("postId", postId);
            params.put("page", page);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }
}