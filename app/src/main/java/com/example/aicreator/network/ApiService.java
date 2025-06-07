package com.example.aicreator.network;

import com.example.aicreator.models.GeneratedImage;
import com.example.aicreator.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API服务接口
 * 定义与后端服务的RESTful API通信方法
 */
public interface ApiService {

    // 用户相关API
    @POST("users/register")
    Call<User> registerUser(@Body User user);
    
    @POST("users/login")
    Call<User> loginUser(@Body Map<String, String> credentials);
    
    @GET("users/{id}")
    Call<User> getUser(@Path("id") int userId);
    
    // 图像生成相关API
    @POST("images/generate")
    Call<GeneratedImage> generateImage(@Body Map<String, Object> params);
    
    @GET("images/user/{userId}")
    Call<List<GeneratedImage>> getUserImages(@Path("userId") int userId);
    
    @GET("images/recent")
    Call<List<GeneratedImage>> getRecentImages(@Query("limit") int limit);
    
    @DELETE("images/{id}")
    Call<Void> deleteImage(@Path("id") int imageId);
    
    // 其他API方法可以根据需要添加
} 