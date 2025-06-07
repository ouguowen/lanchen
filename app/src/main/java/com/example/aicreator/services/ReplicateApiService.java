package com.example.aicreator.services;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.aicreator.models.ReplicateRequest;
import com.example.aicreator.models.ReplicateResponse;

/**
 * Replicate API服务接口
 * 定义与Replicate API通信的方法
 */
public interface ReplicateApiService {
    
    /**
     * 运行模型
     * @param owner 模型所有者
     * @param model 模型ID
     * @param input 模型输入参数
     * @return API响应
     */
    @POST("v1/models/{owner}/{model}/predictions")
    Call<ReplicateResponse> runModel(
            @Path("owner") String owner,
            @Path("model") String model,
            @Body Map<String, Object> input
    );
    
    /**
     * 运行指定版本的模型
     * @param version 模型版本
     * @param input 模型输入参数
     * @return API响应
     */
    @POST("v1/models/versions/{version}/predictions")
    Call<ReplicateResponse> runModelVersion(
            @Path("version") String version,
            @Body Map<String, Object> input
    );
    
    /**
     * 带API密钥的模型运行
     * @param apiKey API密钥
     * @param owner 模型所有者
     * @param model 模型ID
     * @param input 模型输入参数
     * @return API响应
     */
    @POST("v1/models/{owner}/{model}/predictions")
    Call<ReplicateResponse> runModelWithApiKey(
            @Header("Authorization") String apiKey,
            @Path("owner") String owner,
            @Path("model") String model,
            @Body Map<String, Object> input
    );
} 