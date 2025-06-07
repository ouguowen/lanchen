package com.example.aicreator.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.aicreator.database.entity.GeneratedImageEntity;
import com.example.aicreator.models.GeneratedImage;
import com.example.aicreator.repository.ImageRepository;

import java.util.List;

/**
 * 文生图功能的ViewModel
 * 负责处理文生图功能的业务逻辑和数据管理
 */
public class TextToImageViewModel extends AndroidViewModel {

    private final ImageRepository repository;
    
    // UI状态
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MediatorLiveData<GeneratedImage> generatedImage = new MediatorLiveData<>();
    
    // 用户历史图像
    private LiveData<List<GeneratedImageEntity>> userImages;
    
    // 当前用户ID，实际应用中应该从用户会话或登录状态获取
    private int currentUserId = 1; // 默认用户ID
    
    public TextToImageViewModel(@NonNull Application application) {
        super(application);
        
        // 初始化存储库
        repository = new ImageRepository(application);
        
        // 加载用户的历史图像
        userImages = repository.getUserImages(currentUserId);
    }
    
    /**
     * 生成图像
     * @param prompt 文本描述提示
     * @param size 图像尺寸 (例如: "1024x1024")
     * @param creativity 创意度 (通常为1.0-8.0之间的值)
     */
    public void generateImage(String prompt, String size, float creativity) {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        // 解析尺寸字符串
        String[] dimensions = size.split("x");
        int width = Integer.parseInt(dimensions[0]);
        int height = Integer.parseInt(dimensions[1]);
        
        // 调用存储库生成图像
        LiveData<GeneratedImage> result = repository.generateImage(
                currentUserId,
                prompt,
                width,
                height,
                creativity
        );
        
        // 添加结果源
        generatedImage.addSource(result, image -> {
            if (image != null) {
                generatedImage.setValue(image);
            }
            isLoading.setValue(false);
        });
    }
    
    /**
     * 删除生成的图像
     * @param imageId 图像ID
     */
    public void deleteImage(int imageId) {
        repository.deleteImage(imageId);
    }
    
    /**
     * 更新图像收藏状态
     * @param imageId 图像ID
     * @param isFavorite 是否收藏
     */
    public void updateFavoriteStatus(int imageId, boolean isFavorite) {
        repository.updateFavoriteStatus(imageId, isFavorite);
    }
    
    /**
     * 获取生成的图像
     * @return 图像LiveData
     */
    public LiveData<GeneratedImage> getGeneratedImage() {
        return generatedImage;
    }
    
    /**
     * 获取用户的图像历史
     * @return 图像列表LiveData
     */
    public LiveData<List<GeneratedImageEntity>> getUserImages() {
        return userImages;
    }
    
    /**
     * 获取最近生成的图像
     * @param limit 限制数量
     * @return 图像列表LiveData
     */
    public LiveData<List<GeneratedImageEntity>> getRecentImages(int limit) {
        return repository.getRecentImages(limit);
    }
    
    /**
     * 获取加载状态
     * @return 加载状态的LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * 获取错误消息
     * @return 错误消息的LiveData
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * 设置当前用户ID
     * @param userId 用户ID
     */
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        // 切换用户后重新加载图像
        userImages = repository.getUserImages(userId);
    }
    
    /**
     * 搜索图像
     * @param query 搜索关键词
     * @return 图像列表LiveData
     */
    public LiveData<List<GeneratedImageEntity>> searchImages(String query) {
        return repository.searchImages(query);
    }
} 