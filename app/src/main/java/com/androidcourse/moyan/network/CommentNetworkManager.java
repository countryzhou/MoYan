package com.androidcourse.moyan.network;

import android.text.TextUtils;

import com.androidcourse.moyan.model.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 评论网络请求管理类（单例模式）
 * 功能：封装所有与评论相关的服务器通信接口，包括获取评论、发表评论、删除评论、点赞评论等操作
 * 通信方式：通过SocketClient发送JSON格式的请求，并解析服务器返回的JSON响应
 */
public class CommentNetworkManager {

    // ==================== 单例模式相关 ====================
    private static CommentNetworkManager instance;  // 单例实例
    private final Gson gson;  // Gson解析器：用于JSON字符串与Java对象之间的转换

    /**
     * 功能：私有构造函数，实现单例模式
     * 具体实现：初始化Gson解析器实例
     * 注意：私有构造函数确保外部无法直接new创建对象，必须通过getInstance()获取
     */
    private CommentNetworkManager() {
        gson = new Gson();
    }

    /**
     * 功能：获取CommentNetworkManager的单例实例（线程不安全，但Android单线程环境下可用）
     * 具体实现：如果instance为null则创建新实例，否则返回已有实例
     * 设计模式：懒汉式单例（Lazy Initialization）
     *
     * @return CommentNetworkManager的唯一实例
     */
    public static CommentNetworkManager getInstance() {
        if (instance == null) {
            instance = new CommentNetworkManager();
        }
        return instance;
    }

    /**
     * 功能：获取指定帖子的评论列表（支持分页）
     * 具体实现：
     *   1. 构造JSON请求对象，包含type="get_comments"、postId、page、pageSize参数
     *   2. 通过SocketClient发送请求，获取服务器响应
     *   3. 解析响应JSON，检查code字段：
     *      - code==0表示成功，从data.comments数组解析出List<Comment>
     *      - code!=0表示失败，返回null
     *   4. 发生异常时打印堆栈并返回null
     *
     * @param postId 帖子ID，指定要获取哪个帖子的评论
     * @param page 页码（从1开始），用于分页加载
     * @param pageSize 每页数量，控制一次返回多少条评论
     * @return 评论列表（List<Comment>），失败或为空时返回null
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
     * 功能：发表新评论或回复他人的评论
     * 具体实现：
     *   1. 构造JSON请求对象，包含type="add_comment"、postId、userId、content、replyToCommentId
     *   2. 通过SocketClient发送请求
     *   3. 解析响应，code==0表示发表成功，否则失败
     *
     * @param postId 帖子ID，指定评论属于哪个帖子
     * @param userId 当前登录用户ID，用于标识评论的发布者
     * @param content 评论内容（文本）
     * @param replyToCommentId 回复的评论ID：
     *                          - 如果为0，表示这是一条顶级评论（直接回复帖子）
     *                          - 如果大于0，表示这是对某条评论的回复（嵌套评论）
     * @return true-发表成功，false-发表失败
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
     * 功能：删除评论（需要权限验证）
     * 具体实现：
     *   1. 构造JSON请求对象，包含type="delete_comment"、commentId、userId
     *   2. 通过SocketClient发送请求
     *   3. 服务器会验证userId是否有权限删除该评论（只有评论作者或管理员可以删除）
     *   4. 返回删除是否成功
     *
     * @param commentId 要删除的评论ID
     * @param userId 当前登录用户ID，用于权限验证
     * @return true-删除成功，false-删除失败（可能无权限或评论不存在）
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
     * 功能：点赞或取消点赞评论（切换点赞状态）
     * 具体实现：
     *   1. 构造JSON请求对象，包含type="like_comment"、commentId、userId、isLike
     *   2. 通过SocketClient发送请求
     *   3. 服务器会根据isLike的值增加或减少评论的点赞数
     *
     * @param commentId 要操作的评论ID
     * @param userId 当前登录用户ID，用于记录哪个用户点的赞
     * @param isLiked true-表示点赞操作，false-表示取消点赞操作
     * @return true-操作成功，false-操作失败
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
     * 功能：获取指定帖子的评论总数
     * 具体实现：
     *   1. 构造JSON请求对象，包含type="get_comment_count"、postId
     *   2. 发送请求并解析响应
     *   3. 成功时返回data.count字段的数值
     *
     * 使用场景：在帖子列表中显示评论数，或在评论页面顶部显示"共xx条评论"
     *
     * @param postId 帖子ID
     * @return 评论总数，获取失败时返回0（避免空指针）
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
     * 功能：获取指定评论的回复列表（用于嵌套评论的二级回复）
     * 具体实现：
     *   1. 构造JSON请求对象，包含type="get_replies"、commentId、page、pageSize
     *   2. 发送请求并解析响应
     *   3. 成功时从data.replies数组中解析出回复列表
     *
     * 使用场景：当用户点击某条评论的“查看回复”按钮时，加载这条评论下的所有子回复
     *
     * @param commentId 父评论ID，要获取的是这条评论下的回复
     * @param page 页码（从1开始），支持分页加载
     * @param pageSize 每页数量
     * @return 回复列表（List<Comment>），每个Comment对象的replyToCommentId字段指向父评论ID，
     *         失败或无回复时返回null
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