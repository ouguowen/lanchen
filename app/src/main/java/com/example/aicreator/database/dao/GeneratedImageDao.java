package com.example.aicreator.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aicreator.database.entity.GeneratedImageEntity;

import java.util.List;

/**
 * 生成图像数据访问对象
 * 定义生成图像表的数据库操作
 */
@Dao
public interface GeneratedImageDao {
    
    /**
     * 插入图像
     * @param image 图像实体
     * @return 插入的ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(GeneratedImageEntity image);
    
    /**
     * 批量插入图像
     * @param images 图像实体列表
     * @return 插入的ID列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<GeneratedImageEntity> images);
    
    /**
     * 更新图像
     * @param image 图像实体
     */
    @Update
    void update(GeneratedImageEntity image);
    
    /**
     * 删除图像
     * @param image 图像实体
     */
    @Delete
    void delete(GeneratedImageEntity image);
    
    /**
     * 根据ID查询图像
     * @param id 图像ID
     * @return 图像实体
     */
    @Query("SELECT * FROM generated_images WHERE id = :id")
    LiveData<GeneratedImageEntity> getImageById(int id);
    
    /**
     * 获取用户的所有图像
     * @param userId 用户ID
     * @return 图像列表
     */
    @Query("SELECT * FROM generated_images WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<GeneratedImageEntity>> getImagesByUserId(int userId);
    
    /**
     * 获取最近生成的图像
     * @param limit 限制数量
     * @return 图像列表
     */
    @Query("SELECT * FROM generated_images ORDER BY created_at DESC LIMIT :limit")
    LiveData<List<GeneratedImageEntity>> getRecentImages(int limit);
    
    /**
     * 获取收藏的图像
     * @param userId 用户ID
     * @return 图像列表
     */
    @Query("SELECT * FROM generated_images WHERE user_id = :userId AND is_favorite = 1 ORDER BY created_at DESC")
    LiveData<List<GeneratedImageEntity>> getFavoriteImages(int userId);
    
    /**
     * 更新图像收藏状态
     * @param imageId 图像ID
     * @param isFavorite 是否收藏
     */
    @Query("UPDATE generated_images SET is_favorite = :isFavorite WHERE id = :imageId")
    void updateFavoriteStatus(int imageId, boolean isFavorite);
    
    /**
     * 更新本地路径
     * @param imageId 图像ID
     * @param localPath 本地路径
     */
    @Query("UPDATE generated_images SET local_path = :localPath WHERE id = :imageId")
    void updateLocalPath(int imageId, String localPath);
    
    /**
     * 删除用户的所有图像
     * @param userId 用户ID
     */
    @Query("DELETE FROM generated_images WHERE user_id = :userId")
    void deleteAllUserImages(int userId);
    
    /**
     * 搜索图像
     * @param query 搜索关键词
     * @return 图像列表
     */
    @Query("SELECT * FROM generated_images WHERE prompt LIKE '%' || :query || '%' ORDER BY created_at DESC")
    LiveData<List<GeneratedImageEntity>> searchImages(String query);
} 