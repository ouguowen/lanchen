package com.example.aicreator.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aicreator.database.AppDatabase;
import com.example.aicreator.database.dao.GeneratedVideoDao;
import com.example.aicreator.models.GeneratedVideo;
import com.example.aicreator.network.ApiClient;
import com.example.aicreator.network.ApiService;
import com.example.aicreator.utils.SharedPreferencesManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 视频存储库
 * 负责协调本地数据和远程API数据
 */
public class VideoRepository {
    private static final String TAG = "VideoRepository";
    
    private final GeneratedVideoDao videoDao;
    private final ApiService apiService;
    private final Executor executor;
    private final SharedPreferencesManager prefsManager;
    
    /**
     * 构造方法
     * @param context 应用上下文
     */
    public VideoRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        videoDao = database.generatedVideoDao();
        apiService = ApiClient.getInstance().getApiService();
        executor = Executors.newSingleThreadExecutor();
        prefsManager = SharedPreferencesManager.getInstance(context);
    }
    
    /**
     * 获取当前用户的所有视频
     * @return 视频列表LiveData
     */
    public LiveData<List<GeneratedVideo>> getUserVideos() {
        long userId = prefsManager.getCurrentUserId();
        return videoDao.getVideosByUserId(userId);
    }
    
    /**
     * 获取当前用户收藏的视频
     * @return 收藏视频列表LiveData
     */
    public LiveData<List<GeneratedVideo>> getFavoriteVideos() {
        long userId = prefsManager.getCurrentUserId();
        return videoDao.getFavoriteVideosByUserId(userId);
    }
    
    /**
     * 根据ID获取视频
     * @param videoId 视频ID
     * @return 视频LiveData
     */
    public LiveData<GeneratedVideo> getVideoById(int videoId) {
        return videoDao.getVideoById(videoId);
    }
    
    /**
     * 保存视频到本地数据库
     * @param video 要保存的视频
     */
    public void saveVideo(GeneratedVideo video) {
        executor.execute(() -> {
            video.setUserId(prefsManager.getCurrentUserId());
            videoDao.insert(video);
        });
    }
    
    /**
     * 更新视频
     * @param video 要更新的视频
     */
    public void updateVideo(GeneratedVideo video) {
        executor.execute(() -> videoDao.update(video));
    }
    
    /**
     * 删除视频
     * @param video 要删除的视频
     */
    public void deleteVideo(GeneratedVideo video) {
        executor.execute(() -> {
            // 删除本地文件
            if (video.getVideoPath() != null) {
                File videoFile = new File(video.getVideoPath());
                if (videoFile.exists()) {
                    videoFile.delete();
                }
            }
            
            // 从数据库中删除
            videoDao.delete(video);
        });
    }
    
    /**
     * 更新视频收藏状态
     * @param videoId 视频ID
     * @param isFavorite 是否收藏
     */
    public void updateFavoriteStatus(int videoId, boolean isFavorite) {
        executor.execute(() -> videoDao.updateFavoriteStatus(videoId, isFavorite));
    }
    
    /**
     * 生成视频（调用API）
     * @param imagePath 源图片路径
     * @param style 视频风格
     * @param duration 视频时长
     * @param motionIntensity 运动强度
     * @param callback 回调接口
     */
    public void generateVideo(String imagePath, String style, int duration, 
                              int motionIntensity, VideoGenerationCallback callback) {
        MutableLiveData<VideoGenerationResult> resultLiveData = new MutableLiveData<>();
        
        // 准备图片文件
        File imageFile = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", 
                imageFile.getName(), requestFile);
        
        // 准备其他参数
        Map<String, RequestBody> params = new HashMap<>();
        params.put("style", RequestBody.create(MediaType.parse("text/plain"), style));
        params.put("duration", RequestBody.create(MediaType.parse("text/plain"), 
                String.valueOf(duration)));
        params.put("motion_intensity", RequestBody.create(MediaType.parse("text/plain"), 
                String.valueOf(motionIntensity)));
        
        // 发起API请求
        Call<Map<String, Object>> call = apiService.generateVideo(
                "Bearer " + prefsManager.getAccessToken(),
                imagePart,
                params
        );
        
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, 
                                  Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    String videoUrl = (String) result.get("video_url");
                    callback.onSuccess(videoUrl);
                } else {
                    String errorMsg = "API错误: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                String errorMsg = "网络错误: " + t.getMessage();
                Log.e(TAG, errorMsg);
                callback.onError(errorMsg);
            }
        });
    }
    
    /**
     * 视频生成结果类
     */
    public static class VideoGenerationResult {
        private final boolean success;
        private final String videoUrl;
        private final String errorMessage;
        
        public VideoGenerationResult(boolean success, String videoUrl, String errorMessage) {
            this.success = success;
            this.videoUrl = videoUrl;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getVideoUrl() {
            return videoUrl;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    
    /**
     * 视频生成回调接口
     */
    public interface VideoGenerationCallback {
        void onSuccess(String videoUrl);
        void onError(String errorMessage);
    }
} 