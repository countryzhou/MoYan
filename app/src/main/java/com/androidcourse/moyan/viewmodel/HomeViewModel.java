package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.TrendCard;
import com.androidcourse.moyan.repository.PostRepository;

import java.util.List;

/**
 * 首页ViewModel
 * 负责：加载首页数据（趋势卡片 + 新闻列表）
 * 调用：PostRepository
 */
public class HomeViewModel {

    private PostRepository postRepository;
    private Handler mainHandler;

    private List<TrendCard> cachedTrendCards;
    private List<NewsItem> cachedNewsList;

    public HomeViewModel() {
        postRepository = new PostRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 加载趋势卡片数据
     * @param isDebug 是否为Debug/Mock模式
     * @param callback 回调
     */
    public void loadTrendCards(boolean isDebug, TrendCardCallback callback) {
        if (isDebug) {
            // Mock模式：使用本地数据
            List<TrendCard> defaultCards = postRepository.getDefaultTrendCards();
            cachedTrendCards = defaultCards;
            if (callback != null) callback.onSuccess(defaultCards);
            return;
        }

        // 真实模式：从服务器加载
        postRepository.getTrendCards(new PostRepository.RepositoryCallback<List<TrendCard>>() {
            @Override
            public void onResult(List<TrendCard> result) {
                mainHandler.post(() -> {
                    cachedTrendCards = result;
                    if (callback != null) callback.onSuccess(result);
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    // 失败时使用默认数据兜底
                    List<TrendCard> defaultCards = postRepository.getDefaultTrendCards();
                    cachedTrendCards = defaultCards;
                    if (callback != null) callback.onFallback(defaultCards, error);
                });
            }
        });
    }

    /**
     * 加载新闻列表数据
     * @param isDebug 是否为Debug/Mock模式
     * @param callback 回调
     */
    public void loadNewsList(boolean isDebug, NewsListCallback callback) {
        if (isDebug) {
            // Mock模式：使用本地数据
            List<NewsItem> defaultNews = postRepository.getDefaultNewsList();
            cachedNewsList = defaultNews;
            if (callback != null) callback.onSuccess(defaultNews);
            return;
        }

        // 真实模式：从服务器加载
        postRepository.getNewsList(1, 20, new PostRepository.RepositoryCallback<List<NewsItem>>() {
            @Override
            public void onResult(List<NewsItem> result) {
                mainHandler.post(() -> {
                    cachedNewsList = result;
                    if (callback != null) callback.onSuccess(result);
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    // 失败时使用默认数据兜底
                    List<NewsItem> defaultNews = postRepository.getDefaultNewsList();
                    cachedNewsList = defaultNews;
                    if (callback != null) callback.onFallback(defaultNews, error);
                });
            }
        });
    }

    /**
     * 获取缓存的趋势卡片数据
     */
    public List<TrendCard> getCachedTrendCards() {
        return cachedTrendCards;
    }

    /**
     * 获取缓存的新闻列表数据
     */
    public List<NewsItem> getCachedNewsList() {
        return cachedNewsList;
    }

    /**
     * 趋势卡片回调接口
     */
    public interface TrendCardCallback {
        void onSuccess(List<TrendCard> cards);
        void onFallback(List<TrendCard> cards, String error);
    }

    /**
     * 新闻列表回调接口
     */
    public interface NewsListCallback {
        void onSuccess(List<NewsItem> newsList);
        void onFallback(List<NewsItem> newsList, String error);
    }
}