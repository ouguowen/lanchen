package com.example.aicreator.database;

import android.util.Log;

import com.example.aicreator.models.GeneratedImage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成图像数据访问对象
 * 处理生成图像相关的数据库操作
 */
public class GeneratedImageDao {
    private static final String TAG = "GeneratedImageDao";
    
    /**
     * 保存生成的图像
     * @param image 生成图像对象
     * @return 保存成功返回true，否则返回false
     */
    public boolean saveImage(GeneratedImage image) {
        String sql = "INSERT INTO " + DatabaseConfig.TABLE_GENERATED_IMAGES + 
                " (user_id, prompt, image_url, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, image.getUserId());
            pstmt.setString(2, image.getPrompt());
            pstmt.setString(3, image.getImageUrl());
            pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        image.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            Log.e(TAG, "保存图像失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据ID查找图像
     * @param id 图像ID
     * @return 图像对象，如果未找到则返回null
     */
    public GeneratedImage findImageById(int id) {
        String sql = "SELECT * FROM " + DatabaseConfig.TABLE_GENERATED_IMAGES + " WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToImage(rs);
                }
            }
            
            return null;
        } catch (SQLException e) {
            Log.e(TAG, "查询图像失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取用户的所有图像
     * @param userId 用户ID
     * @return 图像列表
     */
    public List<GeneratedImage> getImagesByUserId(int userId) {
        String sql = "SELECT * FROM " + DatabaseConfig.TABLE_GENERATED_IMAGES + 
                " WHERE user_id = ? ORDER BY created_at DESC";
        List<GeneratedImage> images = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    images.add(mapResultSetToImage(rs));
                }
            }
            
            return images;
        } catch (SQLException e) {
            Log.e(TAG, "获取用户图像失败: " + e.getMessage());
            return images;
        }
    }
    
    /**
     * 获取最近生成的图像列表
     * @param limit 限制返回的数量
     * @return 图像列表
     */
    public List<GeneratedImage> getRecentImages(int limit) {
        String sql = "SELECT * FROM " + DatabaseConfig.TABLE_GENERATED_IMAGES + 
                " ORDER BY created_at DESC LIMIT ?";
        List<GeneratedImage> images = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    images.add(mapResultSetToImage(rs));
                }
            }
            
            return images;
        } catch (SQLException e) {
            Log.e(TAG, "获取最近图像失败: " + e.getMessage());
            return images;
        }
    }
    
    /**
     * 删除图像
     * @param imageId 图像ID
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteImage(int imageId) {
        String sql = "DELETE FROM " + DatabaseConfig.TABLE_GENERATED_IMAGES + " WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, imageId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "删除图像失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 删除用户的所有图像
     * @param userId 用户ID
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteUserImages(int userId) {
        String sql = "DELETE FROM " + DatabaseConfig.TABLE_GENERATED_IMAGES + " WHERE user_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "删除用户图像失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 将ResultSet映射为GeneratedImage对象
     * @param rs ResultSet结果集
     * @return GeneratedImage对象
     * @throws SQLException SQL异常
     */
    private GeneratedImage mapResultSetToImage(ResultSet rs) throws SQLException {
        GeneratedImage image = new GeneratedImage();
        image.setId(rs.getInt("id"));
        image.setUserId(rs.getInt("user_id"));
        image.setPrompt(rs.getString("prompt"));
        image.setImageUrl(rs.getString("image_url"));
        image.setCreatedAt(rs.getTimestamp("created_at"));
        return image;
    }
} 