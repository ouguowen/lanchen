package com.example.aicreator.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aicreator.database.AppDatabase;
import com.example.aicreator.database.dao.GeneratedImageDao;
import com.example.aicreator.database.entity.GeneratedImageEntity;
import com.example.aicreator.models.GeneratedImage;
import com.example.aicreator.network.ApiClient;
import com.example.aicreator.network.ApiService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 图像存储库
 * 负责协调本地数据和远程API数据
 */
public class ImageRepository {
    private static final String TAG = "ImageRepository";
    
    private final GeneratedImageDao imageDao;
    private final ApiService apiService;
    private final Executor executor;
    
    public ImageRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        imageDao = db.generatedImageDao();
        apiService = ApiClient.getClient().create(ApiService.class);
        executor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 生成图像
     * @param userId 用户ID
     * @param prompt 提示词
     * @param width 宽度
     * @param height 高度
     * @param creativityLevel 创意等级
     * @return 结果LiveData
     */
    public LiveData<GeneratedImage> generateImage(int userId, String prompt, int width, int height, float creativityLevel) {
        MutableLiveData<GeneratedImage> result = new MutableLiveData<>();
        
        // 构建请求参数
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("user_id", userId);
        params.put("prompt", prompt);
        params.put("width", width);
        params.put("height", height);
        params.put("creativity_level", creativityLevel);
        
        // 发起API请求
        apiService.generateImage(params).enqueue(new Callback<GeneratedImage>() {
            @Override
            public void onResponse(Call<GeneratedImage> call, Response<GeneratedImage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GeneratedImage image = response.body();
                    result.setValue(image);
                    
                    // 将结果保存到本地数据库
                    saveToLocalDb(image);
                } else {
                    Log.e(TAG, "生成图像失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<GeneratedImage> call, Throwable t) {
                Log.e(TAG, "网络请求失败: " + t.getMessage());
            }
        });
        
        return result;
    }
    
    /**
     * 获取用户生成的图像
     * @param userId 用户ID
     * @return 图像列表LiveData
     */
    public LiveData<List<GeneratedImageEntity>> getUserImages(int userId) {
        return imageDao.getImagesByUserId(userId);
    }
    
    /**
     * 获取最近生成的图像
     * @param limit 限制数量
     * @return 图像列表LiveData
     */
    public LiveData<List<GeneratedImageEntity>> getRecentImages(int limit) {
        return imageDao.getRecentImages(limit);
    }
    
    /**
     * 获取收藏的图像
     * @param userId 用户ID
     * @return 图像列表LiveData
     */
    public LiveData<List<GeneratedImageEntity>> getFavoriteImages(int userId) {
        return imageDao.getFavoriteImages(userId);
    }
    
    /**
     * 更新图像收藏状态
     * @param imageId 图像ID
     * @param isFavorite 是否收藏
     */
    public void updateFavoriteStatus(int imageId, boolean isFavorite) {
        executor.execute(() -> {
            imageDao.updateFavoriteStatus(imageId, isFavorite);
        });
    }
    
    /**
     * 删除图像
     * @param imageId 图像ID
     */
    public void deleteImage(int imageId) {
        // 先从本地数据库删除
        executor.execute(() -> {
            GeneratedImageEntity image = imageDao.getImageById(imageId).getValue();
            if (image != null) {
                imageDao.delete(image);
            }
            
            // 然后从远程服务器删除
            apiService.deleteImage(imageId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "从服务器删除图像失败: " + response.message());
                    }
                }
                
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "删除图像网络请求失败: " + t.getMessage());
                }
            });
        });
    }
    
    /**
     * 搜索图像
     * @param query 搜索关键词
     * @return 图像列表LiveData
     */
    public LiveData<List<GeneratedImageEntity>> searchImages(String query) {
        return imageDao.searchImages(query);
    }
    
    /**
     * 将GeneratedImage转换为GeneratedImageEntity并保存到本地数据库
     */
    private void saveToLocalDb(GeneratedImage image) {
        executor.execute(() -> {
            GeneratedImageEntity entity = new GeneratedImageEntity(
                    image.getUserId(),
                    image.getPrompt(),
                    image.getImageUrl()
            );
            entity.setCreatedAt(image.getCreatedAt());
            
            long id = imageDao.insert(entity);
            Log.d(TAG, "图像已保存到本地数据库，ID: " + id);
        });
    }
} 