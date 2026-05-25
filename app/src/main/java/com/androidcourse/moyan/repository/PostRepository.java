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

            // 添加空值检查
            if (response == null || response.isEmpty()) {
                if (callback != null) callback.onError("网络响应为空");
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    // 检查 data 字段是否存在且为 JSONArray
                    if (!jsonResponse.has("data")) {
                        if (callback != null) callback.onError("响应数据格式错误：缺少data字段");
                        return;
                    }

                    Object data = jsonResponse.get("data");
                    JSONArray postsArray;

                    // 根据数据类型处理
                    if (data instanceof JSONArray) {
                        postsArray = (JSONArray) data;
                    } else if (data instanceof JSONObject) {
                        // 如果 data 是对象，尝试获取其中的数组字段
                        JSONObject dataObj = (JSONObject) data;
                        if (dataObj.has("posts")) {
                            postsArray = dataObj.getJSONArray("posts");
                        } else if (dataObj.has("list")) {
                            postsArray = dataObj.getJSONArray("list");
                        } else {
                            // 假设整个对象就是一个帖子详情，包装成数组
                            postsArray = new JSONArray();
                            postsArray.put(dataObj);
                        }
                    } else {
                        if (callback != null) callback.onError("响应数据格式错误：data字段类型不正确");
                        return;
                    }

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

    /**
     * 搜索帖子
     */
    public void searchPosts(String keyword, String tag, String sortBy, int page,
                            RepositoryCallback<List<Post>> callback) {
        new Thread(() -> {
            String response = networkManager.searchPosts(keyword, tag, sortBy, page);

            // 添加空值检查
            if (response == null || response.isEmpty()) {
                if (callback != null) callback.onError("网络响应为空");
                return;
            }

            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    // 检查 data 字段是否存在且为 JSONArray
                    if (!jsonResponse.has("data")) {
                        if (callback != null) callback.onError("响应数据格式错误：缺少data字段");
                        return;
                    }

                    Object data = jsonResponse.get("data");
                    JSONArray postsArray;

                    // 根据数据类型处理
                    if (data instanceof JSONArray) {
                        postsArray = (JSONArray) data;
                    } else if (data instanceof JSONObject) {
                        // 如果 data 是对象，尝试获取其中的数组字段
                        JSONObject dataObj = (JSONObject) data;
                        if (dataObj.has("posts")) {
                            postsArray = dataObj.getJSONArray("posts");
                        } else if (dataObj.has("list")) {
                            postsArray = dataObj.getJSONArray("list");
                        } else {
                            // 假设整个对象就是一个帖子详情，包装成数组
                            postsArray = new JSONArray();
                            postsArray.put(dataObj);
                        }
                    } else {
                        if (callback != null) callback.onError("响应数据格式错误：data字段类型不正确");
                        return;
                    }

                    Type listType = new TypeToken<List<Post>>() {}.getType();
                    List<Post> postList = gson.fromJson(postsArray.toString(), listType);
                    if (callback != null) callback.onResult(postList);
                } else {
                    String errorMsg = jsonResponse.optString("msg", "搜索失败");
                    if (callback != null) callback.onError(errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError("解析搜索结果失败：" + e.getMessage());
            }
        }).start();
    }

    public interface RepositoryCallback<T> {
        void onResult(T result);
        void onError(String error);
    }
}
