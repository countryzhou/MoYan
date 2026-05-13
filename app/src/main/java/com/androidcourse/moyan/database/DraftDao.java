package com.androidcourse.moyan.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.androidcourse.moyan.model.Draft;
import java.util.List;

@Dao
public interface DraftDao {

    @Query("SELECT * FROM drafts ORDER BY updateTime DESC")
    List<Draft> getAllDrafts();

    @Query("SELECT * FROM drafts WHERE id = :draftId")
    Draft getDraftById(int draftId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDraft(Draft draft);

    @Update
    void updateDraft(Draft draft);

    @Delete
    void deleteDraft(Draft draft);

    @Query("DELETE FROM drafts")
    void deleteAllDrafts();
}