package com.example.aicreator.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit网络客户端工具类
 * 用于创建和配置Retrofit实例
 */
public class RetrofitClient {
    private static final String BASE_URL_REPLICATE = "https://api.replicate.com/";
    private static final String BASE_URL_HUGGINGFACE = "https://api-inference.huggingface.co/";
    private static final String BASE_URL_OPENROUTER = "https://openrouter.ai/api/";
    
    private static Retrofit replicateInstance;
    private static Retrofit huggingFaceInstance;
    private static Retrofit openRouterInstance;
    
    /**
     * 获取Replicate API的Retrofit实例
     * @return Retrofit实例
     */
    public static Retrofit getReplicateInstance() {
        if (replicateInstance == null) {
            replicateInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL_REPLICATE)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return replicateInstance;
    }
    
    /**
     * 获取HuggingFace API的Retrofit实例
     * @return Retrofit实例
     */
    public static Retrofit getHuggingFaceInstance() {
        if (huggingFaceInstance == null) {
            huggingFaceInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL_HUGGINGFACE)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return huggingFaceInstance;
    }
    
    /**
     * 获取OpenRouter API的Retrofit实例
     * @return Retrofit实例
     */
    public static Retrofit getOpenRouterInstance() {
        if (openRouterInstance == null) {
            openRouterInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL_OPENROUTER)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return openRouterInstance;
    }
    
    /**
     * 创建OkHttpClient实例
     * @return OkHttpClient实例
     */
    private static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }
} 