package com.example.aicreator.services;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.aicreator.database.GeneratedImageDao;
import com.example.aicreator.models.GeneratedImage;
import com.example.aicreator.models.ReplicateResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 图像服务类
 * 处理图像生成和数据库存储的逻辑
 */
public class ImageService {
    private static final String TAG = "ImageService";
    private final GeneratedImageDao imageDao;
    private final ReplicateApiService apiService;

    public ImageService(ReplicateApiService apiService) {
        this.apiService = apiService;
        this.imageDao = new GeneratedImageDao();
    }
    
    /**
     * 生成图像并保存到数据库
     * @param userId 用户ID
     * @param prompt 图像描述
     * @param width 宽度
     * @param height 高度
     * @param creativityLevel 创意度
     * @param isLoadingLiveData 加载状态LiveData
     * @param errorLiveData 错误信息LiveData
     * @param resultLiveData 结果LiveData
     */
    public void generateAndSaveImage(
            int userId,
            String prompt,
            int width,
            int height,
            float creativityLevel,
            MutableLiveData<Boolean> isLoadingLiveData,
            MutableLiveData<String> errorLiveData,
            MutableLiveData<String> resultLiveData) {
        
        // 设置加载状态
        isLoadingLiveData.postValue(true);
        
        // 构建请求参数
        Map<String, Object> params = buildRequestParams(prompt, width, height, creativityLevel);
        
        // 调用Replicate API生成图像
        apiService.runModel("stability-ai/sdxl", 
                "a85a424be8c4e503f8698d2bc76fb2f544ec8e3f98f55c39677311b47fa5dad4", 
                params)
                .enqueue(new retrofit2.Callback<ReplicateResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<ReplicateResponse> call, 
                                           retrofit2.Response<ReplicateResponse> response) {
                        isLoadingLiveData.postValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ReplicateResponse result = response.body();
                            
                            if (result.isSuccessful() && result.getOutput() != null && !result.getOutput().isEmpty()) {
                                // 获取生成的图像URL
                                String imageUrl = result.getOutput().get(0);
                                
                                // 保存到数据库
                                saveGeneratedImage(userId, prompt, imageUrl);
                                
                                // 更新UI
                                resultLiveData.postValue(imageUrl);
                            } else {
                                // API调用成功但处理失败
                                String error = result.getError();
                                errorLiveData.postValue(error != null ? error : "生成图像失败，请重试");
                            }
                        } else {
                            // API调用失败
                            errorLiveData.postValue("网络请求失败: " + response.message());
                        }
                    }
                    
                    @Override
                    public void onFailure(retrofit2.Call<ReplicateResponse> call, Throwable t) {
                        isLoadingLiveData.postValue(false);
                        errorLiveData.postValue("网络错误: " + t.getMessage());
                    }
                });
    }
    
    /**
     * 构建Replicate API请求参数
     */
    private Map<String, Object> buildRequestParams(String prompt, int width, int height, float creativityLevel) {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("prompt", prompt);
        params.put("width", width);
        params.put("height", height);
        params.put("guidance_scale", creativityLevel);
        params.put("num_outputs", 1);
        params.put("num_inference_steps", 30);
        return params;
    }
    
    /**
     * 保存生成的图像到数据库
     */
    private void saveGeneratedImage(int userId, String prompt, String imageUrl) {
        GeneratedImage image = new GeneratedImage();
        image.setUserId(userId);
        image.setPrompt(prompt);
        image.setImageUrl(imageUrl);
        image.setCreatedAt(new Date());
        
        // 异步保存到数据库
        new Thread(() -> {
            boolean success = imageDao.saveImage(image);
            if (!success) {
                Log.e(TAG, "保存图像到数据库失败");
            }
        }).start();
    }
    
    /**
     * 获取用户生成的图像列表
     * @param userId 用户ID
     * @return 图像列表
     */
    public List<GeneratedImage> getUserImages(int userId) {
        return imageDao.getImagesByUserId(userId);
    }
    
    /**
     * 获取最近生成的图像
     * @param limit 限制数量
     * @return 图像列表
     */
    public List<GeneratedImage> getRecentImages(int limit) {
        return imageDao.getRecentImages(limit);
    }
    
    /**
     * 删除图像
     * @param imageId 图像ID
     * @return 是否删除成功
     */
    public boolean deleteImage(int imageId) {
        return imageDao.deleteImage(imageId);
    }
} 