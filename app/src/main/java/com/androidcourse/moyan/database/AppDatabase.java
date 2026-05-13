package com.androidcourse.moyan.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.androidcourse.moyan.model.Draft;
import com.androidcourse.moyan.model.Tag;

@Database(
        entities = {Draft.class, Tag.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract DraftDao draftDao();
    public abstract TagDao tagDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "moyan_database"
                    ).build();
                }
            }
        }
        return instance;
    }
}