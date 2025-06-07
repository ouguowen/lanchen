package com.example.aicreator.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aicreator.models.GeneratedVideo;

import java.util.List;

/**
 * 生成视频数据访问对象接口
 * 用于访问数据库中的生成视频数据
 */
@Dao
public interface GeneratedVideoDao {
    
    /**
     * 插入视频
     * @param video 要插入的视频
     * @return 插入的视频ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(GeneratedVideo video);
    
    /**
     * 更新视频
     * @param video 要更新的视频
     */
    @Update
    void update(GeneratedVideo video);
    
    /**
     * 删除视频
     * @param video 要删除的视频
     */
    @Delete
    void delete(GeneratedVideo video);
    
    /**
     * 获取所有视频
     * @return 视频列表
     */
    @Query("SELECT * FROM generated_videos ORDER BY createdAt DESC")
    LiveData<List<GeneratedVideo>> getAllVideos();
    
    /**
     * 获取用户的所有视频
     * @param userId 用户ID
     * @return 视频列表
     */
    @Query("SELECT * FROM generated_videos WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<GeneratedVideo>> getVideosByUserId(long userId);
    
    /**
     * 获取收藏的视频
     * @param userId 用户ID
     * @return 收藏的视频列表
     */
    @Query("SELECT * FROM generated_videos WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    LiveData<List<GeneratedVideo>> getFavoriteVideosByUserId(long userId);
    
    /**
     * 根据ID获取视频
     * @param id 视频ID
     * @return 视频
     */
    @Query("SELECT * FROM generated_videos WHERE id = :id")
    LiveData<GeneratedVideo> getVideoById(int id);
    
    /**
     * 更新视频收藏状态
     * @param id 视频ID
     * @param isFavorite 收藏状态
     */
    @Query("UPDATE generated_videos SET isFavorite = :isFavorite WHERE id = :id")
    void updateFavoriteStatus(int id, boolean isFavorite);
} 