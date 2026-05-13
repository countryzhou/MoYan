package com.androidcourse.moyan.repository;

import android.content.Context;
import com.androidcourse.moyan.database.AppDatabase;
import com.androidcourse.moyan.database.DraftDao;
import com.androidcourse.moyan.model.Draft;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DraftRepository {

    private final DraftDao draftDao;
    private final ExecutorService executorService;

    public DraftRepository(Context context) {
        this.draftDao = AppDatabase.getInstance(context).draftDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public interface DraftCallback {
        void onSuccess(List<Draft> drafts);
        void onError(String error);
    }

    public interface SingleDraftCallback {
        void onSuccess(Draft draft);
        void onError(String error);
    }

    public interface InsertCallback {
        void onSuccess(long id);
        void onError(String error);
    }

    // 获取所有草稿
    public void getAllDrafts(DraftCallback callback) {
        executorService.execute(() -> {
            try {
                List<Draft> drafts = draftDao.getAllDrafts();
                if (callback != null) {
                    callback.onSuccess(drafts);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // 根据ID获取草稿
    public void getDraftById(int id, SingleDraftCallback callback) {
        executorService.execute(() -> {
            try {
                Draft draft = draftDao.getDraftById(id);
                if (callback != null) {
                    callback.onSuccess(draft);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // 保存草稿
    public void saveDraft(Draft draft, InsertCallback callback) {
        executorService.execute(() -> {
            try {
                draft.setUpdateTime(System.currentTimeMillis());
                long id = draftDao.insertDraft(draft);
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

    // 更新草稿
    public void updateDraft(Draft draft, InsertCallback callback) {
        executorService.execute(() -> {
            try {
                draft.setUpdateTime(System.currentTimeMillis());
                draftDao.updateDraft(draft);
                if (callback != null) {
                    callback.onSuccess(draft.getId());
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    // 删除草稿
    public void deleteDraft(Draft draft, Runnable onSuccess, Runnable onError) {
        executorService.execute(() -> {
            try {
                draftDao.deleteDraft(draft);
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

    // 删除所有草稿
    public void deleteAllDrafts(Runnable onSuccess, Runnable onError) {
        executorService.execute(() -> {
            try {
                draftDao.deleteAllDrafts();
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