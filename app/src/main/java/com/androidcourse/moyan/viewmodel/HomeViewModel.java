package com.androidcourse.moyan.viewmodel;

import android.os.Handler;
import android.os.Looper;

import com.androidcourse.moyan.model.NewsItem;
import com.androidcourse.moyan.model.TrendCard;
import com.androidcourse.moyan.repository.PostRepository;

import java.util.List;

public class HomeViewModel {

    private PostRepository postRepository;
    private Handler mainHandler;

    public HomeViewModel() {
        postRepository = new PostRepository();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void loadTrendCards(TrendCardCallback callback) {
        postRepository.getTrendCards(new PostRepository.RepositoryCallback<List<TrendCard>>() {
            @Override
            public void onResult(List<TrendCard> result) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(result);
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

    public void loadNewsList(NewsListCallback callback) {
        postRepository.getNewsList(1, 20, new PostRepository.RepositoryCallback<List<NewsItem>>() {
            @Override
            public void onResult(List<NewsItem> result) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(result);
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