package com.androidcourse.moyan.repository;

import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.model.TrendCard;
import com.androidcourse.moyan.network.PostNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;
import com.androidcourse.moyan.utils.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子数据仓库
 * 负责：帖子列表、帖子详情、发帖、搜索 + 匿名发帖逻辑
 * 调用：PostNetworkManager + SharedPrefsHelper
 */
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
     * 获取帖子列表（首页推荐流）
     * @param page 页码
     * @param size 每页数量
     * @param callback 回调
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
                    if (callback != null) {
                        callback.onResult(postList);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取帖子列表失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析帖子列表失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 获取帖子详情
     * 对应API序号9
     * @param postId 帖子ID
     * @param callback 回调
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
                    if (callback != null) {
                        callback.onResult(post);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取帖子详情失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析帖子详情失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 发布帖子
     * @param userId 用户ID
     * @param isAnonymous 是否匿名
     * @param title 标题
     * @param content 内容
     * @param tags 标签
     * @param callback 回调（返回新帖子的postId）
     */
    public void createPost(int userId, boolean isAnonymous, String title,
                           String content, String tags, RepositoryCallback<Integer> callback) {
        new Thread(() -> {
            String response = networkManager.createPost(userId, isAnonymous, title, content, tags);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    int postId = jsonResponse.getInt("data");
                    if (callback != null) {
                        callback.onResult(postId);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "发布帖子失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析发布帖子响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 搜索帖子
     * @param keyword 关键词
     * @param tag 标签
     * @param sortBy 排序方式
     * @param page 页码
     * @param callback 回调
     */
    public void searchPosts(String keyword, String tag, String sortBy, int page,
                            RepositoryCallback<List<Post>> callback) {
        new Thread(() -> {
            String response = networkManager.searchPosts(keyword, tag, sortBy, page);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONArray postsArray = jsonResponse.getJSONArray("data");
                    Type listType = new TypeToken<List<Post>>() {}.getType();
                    List<Post> postList = gson.fromJson(postsArray.toString(), listType);
                    if (callback != null) {
                        callback.onResult(postList);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "搜索失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析搜索结果失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 给帖子评分
     */
    public void ratePost(int postId, int userId, int tagAccuracy, int articleScore,
                         String comment, RepositoryCallback<Boolean> callback) {
        new Thread(() -> {
            String response = networkManager.ratePost(postId, userId, tagAccuracy, articleScore, comment);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    if (callback != null) {
                        callback.onResult(true);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "评分失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析评分响应失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 获取趋势卡片数据（前4条热门帖子）
     * 转换为TrendCard格式
     */
    public void getTrendCards(RepositoryCallback<List<TrendCard>> callback) {
        new Thread(() -> {
            int userId = spHelper.getUserId();
            String response = networkManager.getPostList(1, 4, userId);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONArray posts = jsonResponse.getJSONArray("data");
                    List<TrendCard> cards = new ArrayList<>();
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        TrendCard card = new TrendCard(
                                post.getInt("postId"),
                                post.getString("title"),
                                post.getString("nickname"),
                                TimeUtils.formatRelativeTime(post.getLong("createTime")),
                                android.R.drawable.ic_menu_gallery,
                                post.getInt("replyCount")
                        );
                        cards.add(card);
                    }
                    if (callback != null) {
                        callback.onResult(cards);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取趋势卡片失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析趋势卡片失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 获取新闻列表数据（首页竖向列表）
     * 转换为NewsItem格式
     */
    public void getNewsList(int page, int size, RepositoryCallback<List<NewsItem>> callback) {
        new Thread(() -> {
            int userId = spHelper.getUserId();
            String response = networkManager.getPostList(page, size, userId);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("code") == 0) {
                    JSONArray posts = jsonResponse.getJSONArray("data");
                    List<NewsItem> newsList = new ArrayList<>();
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject post = posts.getJSONObject(i);
                        NewsItem item = new NewsItem(
                                post.getInt("postId"),
                                post.getString("title"),
                                post.getString("content"),
                                post.getString("nickname"),
                                post.getLong("createTime"),
                                android.R.drawable.ic_menu_gallery
                        );
                        item.setCommentCount(post.getInt("replyCount"));
                        newsList.add(item);
                    }
                    if (callback != null) {
                        callback.onResult(newsList);
                    }
                } else {
                    String errorMsg = jsonResponse.optString("msg", "获取新闻列表失败");
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("解析新闻列表失败：" + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 获取默认趋势卡片（Mock/兜底数据）
     */
    public List<TrendCard> getDefaultTrendCards() {
        List<TrendCard> cards = new ArrayList<>();
        cards.add(new TrendCard(1, "热门推荐", "官方工作室",
                "今日热点新闻", android.R.drawable.ic_menu_gallery, 128));
        cards.add(new TrendCard(2, "科技前沿", "科技日报",
                "最新科技动态", android.R.drawable.ic_menu_gallery, 89));
        cards.add(new TrendCard(3, "娱乐八卦", "娱乐周刊",
                "明星最新资讯", android.R.drawable.ic_menu_gallery, 256));
        cards.add(new TrendCard(4, "体育赛事", "体育频道",
                "精彩比赛回顾", android.R.drawable.ic_menu_gallery, 67));
        return cards;
    }

    /**
     * 获取默认新闻列表（Mock/兜底数据）
     */
    public List<NewsItem> getDefaultNewsList() {
        List<NewsItem> newsList = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (int i = 1; i <= 5; i++) {
            NewsItem item = new NewsItem(
                    i,
                    "示例新闻标题 " + i,
                    "这是示例新闻内容，当服务器没有数据时会显示这些内容。",
                    "系统作者",
                    currentTime - i * 3600000L,
                    android.R.drawable.ic_menu_gallery
            );
            item.setCommentCount((int) (Math.random() * 100));
            newsList.add(item);
        }
        return newsList;
    }

    /**
     * 通用回调接口
     */
    public interface RepositoryCallback<T> {
        void onResult(T result);
        void onError(String error);
    }
}