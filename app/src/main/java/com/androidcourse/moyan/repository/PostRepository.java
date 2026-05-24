package com.androidcourse.moyan.repository;

import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.network.PostNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class PostRepository {

    private PostNetworkManager networkManager;
    private SharedPrefsHelper spHelper;
    private Gson gson;

    public PostRepository() {
        networkManager = PostNetworkManager.getInstance();
        spHelper = SharedPrefsHelper.getInstance();
        gson = new Gson();
    }

    /**
     * 获取帖子列表
     */
    public void getPostList(int page, int size, RepositoryCallback<List<Post>> callback) {
        new Thread(() -> {
            int userId = spHelper.getUserId();
            String response = networkManager.getPostList(page, size, userId);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONArray postsArray = jsonResponse.getJSONArray("data");
                    Type listType = new TypeToken<List<Post>>() {}.getType();
                    List<Post> postList = gson.fromJson(postsArray.toString(), listType);
                    if (callback != null) callback.onResult(postList);
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取帖子列表失败");
                    if (callback != null) callback.onError(errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError("解析帖子列表失败：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 获取帖子详情
     */
    public void getPostDetail(int postId, RepositoryCallback<Post> callback) {
        new Thread(() -> {
            int userId = spHelper.getUserId();
            String response = networkManager.getPostDetail(postId, userId);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    Post post = gson.fromJson(data.toString(), Post.class);
                    if (callback != null) callback.onResult(post);
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取帖子详情失败");
                    if (callback != null) callback.onError(errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError("解析帖子详情失败：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 发布帖子
     */
    public void createPost(int userId, boolean isAnonymous, String title, String content, String tags,
                           RepositoryCallback<Integer> callback) {
        new Thread(() -> {
            String response = networkManager.createPost(userId, isAnonymous, title, content, tags, null);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    int postId = jsonResponse.getInt("data");
                    if (callback != null) callback.onResult(postId);
                } else {
                    String errorMsg = jsonResponse.optString("msg", "发布失败");
                    if (callback != null) callback.onError(errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError("发布失败：" + e.getMessage());
            }
        }).start();
    }

    public interface RepositoryCallback<T> {
        void onResult(T result);
        void onError(String error);
    }
}