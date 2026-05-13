package com.androidcourse.moyan.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.androidcourse.moyan.database.Converters;
import java.util.List;

@Entity(tableName = "drafts")
public class Draft {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String content;

    @TypeConverters(Converters.class)
    private List<String> imagePaths;

    @TypeConverters(Converters.class)
    private List<String> tags;

    private boolean isAnonymous;
    private long createTime;
    private long updateTime;

    public Draft() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
}