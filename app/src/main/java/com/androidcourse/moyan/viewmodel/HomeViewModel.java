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
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;

    public HomeViewModel() {
        postRepository = new PostRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

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

    public void loadNewsList(int page, int size, NewsListCallback callback) {
        postRepository.getPostList(page, size, new PostRepository.RepositoryCallback<List<Post>>() {
            @Override
            public void onResult(List<Post> result) {
                List<NewsItem> newsList = new ArrayList<>();
                int startIndex = Math.min(4, result.size());
                for (int i = startIndex; i < result.size(); i++) {
                    Post post = result.get(i);
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

    public void loadMoreNews(int nextPage, NewsListCallback callback) {
        postRepository.getPostList(nextPage, PAGE_SIZE, new PostRepository.RepositoryCallback<List<Post>>() {
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

    public void resetPagination() {
        currentPage = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void incrementPage() {
        currentPage++;
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
