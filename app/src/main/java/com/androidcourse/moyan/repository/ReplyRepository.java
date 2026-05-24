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
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONArray repliesArray = jsonResponse.getJSONArray("data");
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