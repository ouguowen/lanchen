package com.example.aicreator.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aicreator.database.AppDatabase;
import com.example.aicreator.database.dao.UserDao;
import com.example.aicreator.database.entity.UserEntity;
import com.example.aicreator.models.User;
import com.example.aicreator.network.ApiClient;
import com.example.aicreator.network.ApiService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户存储库
 * 负责协调本地数据和远程API数据
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    
    private final UserDao userDao;
    private final ApiService apiService;
    private final Executor executor;
    
    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        apiService = ApiClient.getClient().create(ApiService.class);
        executor = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 注册结果LiveData
     */
    public LiveData<User> register(String username, String password, String email) {
        MutableLiveData<User> result = new MutableLiveData<>();
        
        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreatedAt(new Date());
        
        // 发起API请求
        apiService.registerUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User registeredUser = response.body();
                    result.setValue(registeredUser);
                    
                    // 保存到本地数据库
                    saveToLocalDb(registeredUser);
                } else {
                    Log.e(TAG, "注册失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "网络请求失败: " + t.getMessage());
            }
        });
        
        return result;
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果LiveData
     */
    public LiveData<User> login(String username, String password) {
        MutableLiveData<User> result = new MutableLiveData<>();
        
        // 创建登录参数
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
        
        // 发起API请求
        apiService.loginUser(credentials).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User loggedInUser = response.body();
                    result.setValue(loggedInUser);
                    
                    // 更新本地数据库
                    loggedInUser.setLastLogin(new Date());
                    saveToLocalDb(loggedInUser);
                    
                    // 更新最后登录时间
                    executor.execute(() -> {
                        userDao.updateLastLoginTime(loggedInUser.getId(), System.currentTimeMillis());
                    });
                } else {
                    Log.e(TAG, "登录失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "网络请求失败: " + t.getMessage());
            }
        });
        
        return result;
    }
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息LiveData
     */
    public LiveData<UserEntity> getUserById(int userId) {
        return userDao.getUserById(userId);
    }
    
    /**
     * 更新用户信息
     * @param user 用户对象
     */
    public void updateUser(User user) {
        executor.execute(() -> {
            // 保存到本地数据库
            UserEntity entity = userEntityFromUser(user);
            userDao.update(entity);
            
            // 更新到远程服务器
            apiService.updateUser(user.getId(), user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "更新用户信息失败: " + response.message());
                    }
                }
                
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "更新用户信息网络请求失败: " + t.getMessage());
                }
            });
        });
    }
    
    /**
     * 将User转换为UserEntity
     */
    private UserEntity userEntityFromUser(User user) {
        UserEntity entity = new UserEntity(
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
        entity.setId(user.getId());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setLastLogin(user.getLastLogin());
        return entity;
    }
    
    /**
     * 将User对象保存到本地数据库
     */
    private void saveToLocalDb(User user) {
        executor.execute(() -> {
            UserEntity entity = userEntityFromUser(user);
            long id = userDao.insert(entity);
            Log.d(TAG, "用户已保存到本地数据库，ID: " + id);
        });
    }
} 