package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.Post;
import com.androidcourse.moyan.model.TrendCard;
import com.androidcourse.moyan.repository.PostRepository;
import com.androidcourse.moyan.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel {

    private PostRepository postRepository;
    private Handler mainHandler;

    public HomeViewModel() {
        postRepository = new PostRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 加载趋势卡片（前4条热门帖子）
     */
    public void loadTrendCards(TrendCardCallback callback) {
        postRepository.getPostList(1, 4, new PostRepository.RepositoryCallback<List<Post>>() {
            @Override
            public void onResult(List<Post> result) {
                List<TrendCard> cards = new ArrayList<>();
                for (Post post : result) {
                    TrendCard card = new TrendCard();
                    card.setPostId(post.getPostId());
                    card.setTitle(post.getTitle());
                    card.setAuthor(post.getDisplayName());
                    card.setTime(TimeUtils.formatRelativeTime(post.getPostTime()));
                    card.setCommentCount(post.getReplyCount());
                    cards.add(card);
                }
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(cards);
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onError(error);
                });
            }
        });
    }

    /**
     * 加载新闻列表
     */
    public void loadNewsList(NewsListCallback callback) {
        postRepository.getPostList(1, 20, new PostRepository.RepositoryCallback<List<Post>>() {
            @Override
            public void onResult(List<Post> result) {
                List<NewsItem> newsList = new ArrayList<>();
                for (Post post : result) {
                    NewsItem item = new NewsItem();
                    item.setId(post.getPostId());
                    item.setTitle(post.getTitle());
                    item.setAuthor(post.getDisplayName());
                    item.setPublishTime(post.getPostTime());
                    item.setCommentCount(post.getReplyCount());
                    newsList.add(item);
                }
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(newsList);
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onError(error);
                });
            }
        });
    }

    public interface TrendCardCallback {
        void onSuccess(List<TrendCard> cards);
        void onError(String error);
    }

    public interface NewsListCallback {
        void onSuccess(List<NewsItem> newsList);
        void onError(String error);
    }
}