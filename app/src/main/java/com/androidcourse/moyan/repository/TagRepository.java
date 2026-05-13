package com.androidcourse.moyan.repository;

import android.content.Context;
import com.androidcourse.moyan.database.AppDatabase;
import com.androidcourse.moyan.database.TagDao;
import com.androidcourse.moyan.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TagRepository {

    private final TagDao tagDao;
    private final ExecutorService executorService;

    // 默认标签数据
    private static final String[] DEFAULT_TAGS = {"日常", "旅行", "美食", "学习", "工作", "生活"};

    public TagRepository(Context context) {
        this.tagDao = AppDatabase.getInstance(context).tagDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface TagCallback {
        void onSuccess(List<Tag> tags);
        void onError(String error);
    }

    public interface SingleTagCallback {
        void onSuccess(Tag tag);
        void onError(String error);
    }

    public interface InsertCallback {
        void onSuccess(long id);
        void onError(String error);
    }

    // 初始化默认标签
    public void initDefaultTags(Runnable onComplete) {
        executorService.execute(() -> {
            try {
                List<Tag> defaultTags = tagDao.getDefaultTags();
                if (defaultTags == null || defaultTags.isEmpty()) {
                    for (String tagName : DEFAULT_TAGS) {
                        Tag tag = new Tag(tagName, false);
                        tagDao.insertTag(tag);
                    }
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 获取所有标签
    public void getAllTags(TagCallback callback) {
        executorService.execute(() -> {
            try {
                List<Tag> tags = tagDao.getAllTags();
                if (callback != null) {
                    callback.onSuccess(tags != null ? tags : new ArrayList<>());
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // 获取默认标签
    public void getDefaultTags(TagCallback callback) {
        executorService.execute(() -> {
            try {
                List<Tag> tags = tagDao.getDefaultTags();
                if (callback != null) {
                    callback.onSuccess(tags != null ? tags : new ArrayList<>());
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // 获取自定义标签
    public void getCustomTags(TagCallback callback) {
        executorService.execute(() -> {
            try {
                List<Tag> tags = tagDao.getCustomTags();
                if (callback != null) {
                    callback.onSuccess(tags != null ? tags : new ArrayList<>());
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // 添加标签
    public void addTag(String tagName, boolean isCustom, InsertCallback callback) {
        executorService.execute(() -> {
            try {
                // 检查是否已存在
                Tag existing = tagDao.findTagByName(tagName);
                if (existing != null) {
                    if (callback != null) {
                        callback.onSuccess(existing.getId());
                    }
                    return;
                }
                Tag tag = new Tag(tagName, isCustom);
                long id = tagDao.insertTag(tag);
                if (callback != null) {
                    callback.onSuccess(id);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // 删除标签
    public void deleteTag(Tag tag, Runnable onSuccess, Runnable onError) {
        executorService.execute(() -> {
            try {
                tagDao.deleteTag(tag);
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception e) {
                if (onError != null) {
                    onError.run();
                }
            }
        });
    }
}