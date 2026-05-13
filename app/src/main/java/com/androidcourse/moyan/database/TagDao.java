package com.androidcourse.moyan.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.androidcourse.moyan.model.Tag;
import java.util.List;

@Dao
public interface TagDao {

    @Query("SELECT * FROM tags ORDER BY isCustom ASC, createTime DESC")
    List<Tag> getAllTags();

    @Query("SELECT * FROM tags WHERE isCustom = 0 ORDER BY createTime DESC")
    List<Tag> getDefaultTags();

    @Query("SELECT * FROM tags WHERE isCustom = 1 ORDER BY createTime DESC")
    List<Tag> getCustomTags();

    @Query("SELECT * FROM tags WHERE name = :tagName LIMIT 1")
    Tag findTagByName(String tagName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTag(Tag tag);

    @Delete
    void deleteTag(Tag tag);

    @Query("DELETE FROM tags WHERE isCustom = 1")
    void deleteAllCustomTags();
}