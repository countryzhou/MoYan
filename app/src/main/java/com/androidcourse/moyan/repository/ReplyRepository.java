package com.androidcourse.moyan.repository;

import com.androidcourse.moyan.model.Reply;
import com.androidcourse.moyan.network.ReplyNetworkManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class ReplyRepository {

    private ReplyNetworkManager networkManager;
    private Gson gson;

    public ReplyRepository() {
        networkManager = ReplyNetworkManager.getInstance();
        gson = new Gson();
    }

    /**
     * 获取回复列表
     */
    public void getReplies(int postId, int page, RepositoryCallback<List<Reply>> callback) {
        new Thread(() -> {
            String response = networkManager.getReplies(postId, page);

            // 添加空值检查
            if (response == null || response.isEmpty()) {
                if (callback != null) callback.onError("网络响应为空");
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    // 检查 data 字段是否存在
                    if (!jsonResponse.has("data")) {
                        if (callback != null) callback.onError("响应数据格式错误：缺少data字段");
                        return;
                    }

                    Object data = jsonResponse.get("data");
                    JSONArray repliesArray;

                    // 根据数据类型处理
                    if (data instanceof JSONArray) {
                        repliesArray = (JSONArray) data;
                    } else if (data instanceof JSONObject) {
                        // 如果 data 是对象，尝试获取其中的数组字段
                        JSONObject dataObj = (JSONObject) data;
                        if (dataObj.has("replies")) {
                            repliesArray = dataObj.getJSONArray("replies");
                        } else if (dataObj.has("list")) {
                            repliesArray = dataObj.getJSONArray("list");
                        } else {
                            // 假设整个对象就是一个回复详情，包装成数组
                            repliesArray = new JSONArray();
                            repliesArray.put(dataObj);
                        }
                    } else {
                        if (callback != null) callback.onError("响应数据格式错误：data字段类型不正确");
                        return;
                    }

                    Type listType = new TypeToken<List<Reply>>() {}.getType();
                    List<Reply> replyList = gson.fromJson(repliesArray.toString(), listType);
                    if (callback != null) callback.onResult(replyList);
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取回复失败");
                    if (callback != null) callback.onError(errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError("解析回复列表失败：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 发布回复
     */
    public void createReply(int postId, int userId, boolean isAnonymous, String content,
                            RepositoryCallback<Integer> callback) {
        new Thread(() -> {
            String response = networkManager.createReply(postId, userId, isAnonymous, content);

            // 添加空值检查
            if (response == null || response.isEmpty()) {
                if (callback != null) callback.onError("网络响应为空");
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    int replyId = jsonResponse.getInt("data");
                    if (callback != null) callback.onResult(replyId);
                } else {
                    String errorMsg = jsonResponse.optString("msg", "发表回复失败");
                    if (callback != null) callback.onError(errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError("发表回复失败：" + e.getMessage());
            }
        }).start();
    }

    public interface RepositoryCallback<T> {
        void onResult(T result);
        void onError(String error);
    }
}
