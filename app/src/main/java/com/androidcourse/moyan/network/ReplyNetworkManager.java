package com.androidcourse.moyan.network;

import org.json.JSONObject;

/**
 * 回复相关网络请求管理器
 * 负责：发布回复、获取回复列表
 */
public class ReplyNetworkManager {

    private static ReplyNetworkManager instance;

    private ReplyNetworkManager() {}

    public static ReplyNetworkManager getInstance() {
        if (instance == null) {
            instance = new ReplyNetworkManager();
        }
        return instance;
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
            request.put("action", "createReply");

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
    public String getReplies(int postId, int page) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "getReplies");

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
