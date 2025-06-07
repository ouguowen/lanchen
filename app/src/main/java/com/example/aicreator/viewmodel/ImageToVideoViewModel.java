package com.example.aicreator.viewmodel;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aicreator.models.GeneratedVideo;
import com.example.aicreator.repository.VideoRepository;
import com.example.aicreator.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图生视频ViewModel
 * 负责处理图生视频业务逻辑
 */
public class ImageToVideoViewModel extends AndroidViewModel {
    private static final String TAG = "ImageToVideoViewModel";
    
    private final VideoRepository videoRepository;
    private final Executor executor;
    
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<File> selectedImageFile = new MutableLiveData<>();
    private final MutableLiveData<Uri> generatedVideoUri = new MutableLiveData<>();
    
    /**
     * 构造方法
     * @param application 应用程序实例
     */
    public ImageToVideoViewModel(@NonNull Application application) {
        super(application);
        videoRepository = new VideoRepository(application);
        executor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 获取用户的所有视频
     * @return 视频列表LiveData
     */
    public LiveData<List<GeneratedVideo>> getUserVideos() {
        return videoRepository.getUserVideos();
    }
    
    /**
     * 获取加载状态
     * @return 加载状态LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * 获取错误消息
     * @return 错误消息LiveData
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * 获取成功消息
     * @return 成功消息LiveData
     */
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }
    
    /**
     * 获取选择的图片文件
     * @return 图片文件LiveData
     */
    public LiveData<File> getSelectedImageFile() {
        return selectedImageFile;
    }
    
    /**
     * 获取生成的视频URI
     * @return 视频URI LiveData
     */
    public LiveData<Uri> getGeneratedVideoUri() {
        return generatedVideoUri;
    }
    
    /**
     * 设置选择的图片URI
     * @param imageUri 图片URI
     */
    public void setSelectedImageUri(Uri imageUri) {
        executor.execute(() -> {
            try {
                File imageFile = FileUtils.copyUriToTempFile(
                        getApplication().getContentResolver(),
                        imageUri,
                        "image_source",
                        ".jpg");
                selectedImageFile.postValue(imageFile);
            } catch (IOException e) {
                Log.e(TAG, "无法复制图片文件: " + e.getMessage());
                errorMessage.postValue("无法处理所选图片");
            }
        });
    }
    
    /**
     * 生成视频
     * @param style 视频风格
     * @param duration 视频时长
     * @param motionIntensity 运动强度
     */
    public void generateVideo(String style, int duration, int motionIntensity) {
        File imageFile = selectedImageFile.getValue();
        if (imageFile == null || !imageFile.exists()) {
            errorMessage.setValue("请先选择一张图片");
            return;
        }
        
        isLoading.setValue(true);
        
        videoRepository.generateVideo(
                imageFile.getAbsolutePath(),
                style,
                duration,
                motionIntensity,
                new VideoRepository.VideoGenerationCallback() {
                    @Override
                    public void onSuccess(String videoUrl) {
                        downloadVideo(videoUrl, style, duration, motionIntensity, imageFile.getAbsolutePath());
                    }
                    
                    @Override
                    public void onError(String errorMsg) {
                        isLoading.postValue(false);
                        errorMessage.postValue(errorMsg);
                    }
                }
        );
    }
    
    /**
     * 下载生成的视频
     * @param videoUrl 视频URL
     * @param style 视频风格
     * @param duration 视频时长
     * @param motionIntensity 运动强度
     * @param sourceImagePath 源图片路径
     */
    private void downloadVideo(String videoUrl, String style, int duration, 
                              int motionIntensity, String sourceImagePath) {
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(videoUrl)
                    .build();
            
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    // 保存到应用内部存储
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                            .format(new Date());
                    File videoFile = new File(getApplication().getFilesDir(), 
                            "video_" + timestamp + ".mp4");
                    
                    try (InputStream inputStream = response.body().byteStream();
                         FileOutputStream outputStream = new FileOutputStream(videoFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.flush();
                    }
                    
                    // 保存到数据库
                    GeneratedVideo generatedVideo = new GeneratedVideo();
                    generatedVideo.setVideoPath(videoFile.getAbsolutePath());
                    generatedVideo.setSourceImagePath(sourceImagePath);
                    generatedVideo.setVideoStyle(style);
                    generatedVideo.setDuration(duration);
                    generatedVideo.setMotionIntensity(motionIntensity);
                    
                    videoRepository.saveVideo(generatedVideo);
                    
                    // 发送成功消息和视频URI
                    Uri videoUri = Uri.fromFile(videoFile);
                    generatedVideoUri.postValue(videoUri);
                    successMessage.postValue("视频生成成功");
                } else {
                    errorMessage.postValue("下载视频失败: " + response.code());
                }
            } catch (IOException e) {
                Log.e(TAG, "下载视频异常: " + e.getMessage());
                errorMessage.postValue("下载视频失败: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * 保存视频到公共目录
     * @param videoUri 视频URI
     * @return 保存后的URI
     */
    public void saveVideoToGallery(Uri videoUri) {
        executor.execute(() -> {
            ContentResolver resolver = getApplication().getContentResolver();
            Uri externalUri;
            ContentValues values = new ContentValues();
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String filename = "AICreator_" + timestamp + ".mp4";
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                externalUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                values.put(MediaStore.Video.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.RELATIVE_PATH, 
                        Environment.DIRECTORY_MOVIES + "/AICreator");
                values.put(MediaStore.Video.Media.IS_PENDING, 1);
            } else {
                File directory = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES + "/AICreator");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                
                File file = new File(directory, filename);
                values.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.DISPLAY_NAME, filename);
                externalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
            
            try {
                Uri insertUri = resolver.insert(externalUri, values);
                if (insertUri != null) {
                    try (InputStream in = resolver.openInputStream(videoUri);
                         OutputStream out = resolver.openOutputStream(insertUri)) {
                        if (in == null || out == null) {
                            throw new IOException("无法打开流");
                        }
                        
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        out.flush();
                        
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.clear();
                            values.put(MediaStore.Video.Media.IS_PENDING, 0);
                            resolver.update(insertUri, values, null, null);
                        }
                        
                        successMessage.postValue("视频已保存到相册");
                    }
                } else {
                    errorMessage.postValue("保存视频失败");
                }
            } catch (IOException e) {
                Log.e(TAG, "保存视频异常: " + e.getMessage());
                errorMessage.postValue("保存视频失败: " + e.getMessage());
            }
        });
    }
    
    /**
     * 收藏/取消收藏视频
     * @param video 视频对象
     * @param isFavorite 是否收藏
     */
    public void toggleFavorite(GeneratedVideo video, boolean isFavorite) {
        if (video != null) {
            videoRepository.updateFavoriteStatus(video.getId(), isFavorite);
        }
    }
} 