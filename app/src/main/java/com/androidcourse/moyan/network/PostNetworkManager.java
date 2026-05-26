package com.androidcourse.moyan.network;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;

/**
 * 帖子相关网络请求管理器
 * 负责：发布帖子、获取帖子列表、获取帖子详情、搜索帖子、评分、打赏、举报
 */
public class PostNetworkManager {

    private static PostNetworkManager instance;

    private PostNetworkManager() {}

    public static PostNetworkManager getInstance() {
        if (instance == null) {
            instance = new PostNetworkManager();
        }
        return instance;
    }

    /**
     * 发布帖子（不带图片）
     * @param userId 用户ID
     * @param isAnonymous 是否匿名
     * @param title 标题
     * @param content 内容
     * @param tags 标签（逗号分隔）
     * @return 服务端响应JSON字符串
     */
    public String createPost(int userId, boolean isAnonymous, String title,
                             String content, String tags) {
        // 调用带图片的方法，传入null
        return createPost(userId, isAnonymous, title, content, tags, null);
    }

    /**
     * 发布帖子（带图片）
     * @param userId 用户ID
     * @param isAnonymous 是否匿名
     * @param title 标题
     * @param content 内容
     * @param tags 标签（逗号分隔）
     * @param imagePaths 图片路径列表
     * @return 服务端响应JSON字符串
     */
    public String createPost(int userId, boolean isAnonymous, String title,
                             String content, String tags, List<String> imagePaths) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "createPost");

            JSONObject params = new JSONObject();
            params.put("userId", userId);
            params.put("isAnonymous", isAnonymous);
            params.put("title", title == null || title.isEmpty() ? content.substring(0, Math.min(50, content.length())) : title);
            params.put("content", content);
            params.put("tags", tags == null ? "" : tags);

            // 注意：根据你的API文档，似乎不需要传递图片路径
            // 如果需要传递图片，取消下面的注释
        /*
        if (imagePaths != null && !imagePaths.isEmpty()) {
            JSONArray imagesArray = new JSONArray();
            for (String path : imagePaths) {
                imagesArray.put(path);
            }
            params.put("imagePaths", imagesArray);
        }
        */

            request.put("params", params);

            String requestStr = request.toString();
            android.util.Log.d("PostNetworkManager", "发送请求: " + requestStr);

            String response = SocketClient.getInstance().sendRequest(requestStr);
            android.util.Log.d("PostNetworkManager", "收到响应: " + response);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取帖子列表（首页推荐流）
     * @param page 页码
     * @param size 每页数量
     * @param userId 当前用户ID（可选，用于判断点赞状态）
     * @return 服务端响应JSON字符串
     */
    public String getPostList(int page, int size, int userId) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "getPostList");

            JSONObject params = new JSONObject();
            params.put("page", page);
            params.put("size", size);
            params.put("userId", userId);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取帖子详情
     * @param postId 帖子ID
     * @param userId 当前用户ID（可选，用于判断点赞/评分状态）
     * @return 服务端响应JSON字符串
     */
    public String getPostDetail(int postId, int userId) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "getPostDetail");

            JSONObject params = new JSONObject();
            params.put("postId", postId);
            params.put("userId", userId);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param tag 标签
     * @param sortBy 排序方式：time/hot/score
     * @param page 页码
     * @return 服务端响应JSON字符串
     */
    public String searchPosts(String keyword, String tag, String sortBy, int page) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "searchPosts");

            JSONObject params = new JSONObject();
            params.put("keyword", keyword);
            params.put("tag", tag);
            params.put("sortBy", sortBy);
            params.put("page", page);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 给帖子评分
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param tagAccuracy 标签准确度（1-5）
     * @param articleScore 文章评分（1-5）
     * @param comment 评价内容
     * @return 服务端响应JSON字符串
     */
    public String ratePost(int postId, int userId, int tagAccuracy, int articleScore, String comment) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "ratePost");

            JSONObject params = new JSONObject();
            params.put("postId", postId);
            params.put("userId", userId);
            params.put("tagAccuracy", tagAccuracy);
            params.put("articleScore", articleScore);
            params.put("comment", comment);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 打赏帖子
     * @param postId 帖子ID
     * @param fromUserId 打赏用户ID
     * @param amount 金额（元）
     * @return 服务端响应JSON字符串
     */
    public String tipPost(int postId, int fromUserId, int amount) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "tipPost");

            JSONObject params = new JSONObject();
            params.put("postId", postId);
            params.put("fromUserId", fromUserId);
            params.put("amount", amount);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 举报内容
     * @param reporterId 举报人ID
     * @param targetType 目标类型（1=帖子，2=回复）
     * @param targetId 目标ID
     * @param reason 举报原因
     * @return 服务端响应JSON字符串
     */
    public String report(int reporterId, int targetType, int targetId, String reason) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "report");

            JSONObject params = new JSONObject();
            params.put("reporterId", reporterId);
            params.put("targetType", targetType);
            params.put("targetId", targetId);
            params.put("reason", reason);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取今日互动任务
     * @return 服务端响应JSON字符串
     */
    public String getTodayTask() {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "getTodayTask");
            request.put("params", new JSONObject());

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

// ... existing code ...

    /**
     * 提交任务回答
     * @param taskId 任务ID
     * @param userId 用户ID
     * @param content 回答内容
     * @return 服务端响应JSON字符串
     */
    public String submitTaskAnswer(int taskId, int userId, String content) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "submitTaskAnswer");

            JSONObject params = new JSONObject();
            params.put("taskId", taskId);
            params.put("userId", userId);
            params.put("content", content);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }

    /**
     * 获取用户发布的帖子
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 服务端响应JSON字符串
     */
    public String getPostsByUserId(int userId, int page, int size) {
        try {
            JSONObject request = new JSONObject();
            request.put("action", "getPostsByUserId");

            JSONObject params = new JSONObject();
            params.put("userId", userId);
            params.put("page", page);
            params.put("size", size);
            request.put("params", params);

            return SocketClient.getInstance().sendRequest(request.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\":1,\"msg\":\"请求构建失败：" + e.getMessage() + "\",\"data\":null}";
        }
    }
}