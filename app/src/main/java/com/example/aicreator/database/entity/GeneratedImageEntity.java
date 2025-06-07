package com.example.aicreator.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

/**
 * 生成图像实体类
 * 对应Room数据库中的生成图像表
 */
@Entity(
    tableName = "generated_images",
    foreignKeys = @ForeignKey(
        entity = UserEntity.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("user_id")}
)
public class GeneratedImageEntity {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "user_id")
    private int userId;
    
    @ColumnInfo(name = "prompt")
    private String prompt;
    
    @ColumnInfo(name = "image_url")
    private String imageUrl;
    
    @ColumnInfo(name = "created_at")
    private Date createdAt;
    
    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;
    
    @ColumnInfo(name = "local_path")
    private String localPath;

    // 构造函数
    public GeneratedImageEntity() {
    }

    public GeneratedImageEntity(int userId, String prompt, String imageUrl) {
        this.userId = userId;
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.createdAt = new Date();
        this.isFavorite = false;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    public String getLocalPath() {
        return localPath;
    }
    
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
} 