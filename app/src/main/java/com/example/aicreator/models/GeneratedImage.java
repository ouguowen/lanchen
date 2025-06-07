package com.example.aicreator.models;

import java.util.Date;

/**
 * 生成图像模型类
 * 用于API通信和业务逻辑处理
 */
public class GeneratedImage {
    private int id;
    private int userId;
    private String prompt;
    private String imageUrl;
    private Date createdAt;
    private boolean isFavorite;
    private String localPath;

    // 构造函数
    public GeneratedImage() {
    }

    public GeneratedImage(int id, int userId, String prompt, String imageUrl, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.prompt = prompt;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
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

    @Override
    public String toString() {
        return "GeneratedImage{" +
                "id=" + id +
                ", userId=" + userId +
                ", prompt='" + prompt + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", isFavorite=" + isFavorite +
                ", localPath='" + localPath + '\'' +
                '}';
    }
} 