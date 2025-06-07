package com.example.aicreator.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.aicreator.database.dao.GeneratedImageDao;
import com.example.aicreator.database.dao.GeneratedVideoDao;
import com.example.aicreator.database.dao.UserDao;
import com.example.aicreator.database.entity.GeneratedImageEntity;
import com.example.aicreator.database.entity.UserEntity;
import com.example.aicreator.database.util.DateConverter;
import com.example.aicreator.models.GeneratedVideo;

/**
 * Room数据库类
 * 用于本地数据缓存
 */
@Database(
    entities = {UserEntity.class, GeneratedImageEntity.class, GeneratedVideo.class},
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "aicreator.db";
    private static volatile AppDatabase instance;
    
    // 数据访问对象(DAO)
    public abstract UserDao userDao();
    public abstract GeneratedImageDao generatedImageDao();
    public abstract GeneratedVideoDao generatedVideoDao();
    
    /**
     * 获取数据库实例（单例模式）
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration() // 升级时如果没有迁移策略，则重建数据库
                    .build();
        }
        return instance;
    }
} 