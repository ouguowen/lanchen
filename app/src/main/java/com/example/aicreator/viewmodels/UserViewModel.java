package com.example.aicreator.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.aicreator.database.entity.UserEntity;
import com.example.aicreator.models.User;
import com.example.aicreator.repository.UserRepository;

/**
 * 用户ViewModel
 * 负责处理用户相关的业务逻辑
 */
public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;
    
    // 登录结果
    private final MutableLiveData<User> loginResult = new MutableLiveData<>();
    
    // 注册结果
    private final MutableLiveData<User> registerResult = new MutableLiveData<>();
    
    // 错误消息
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    // 当前用户
    private LiveData<UserEntity> currentUser;
    
    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    public void login(String username, String password) {
        LiveData<User> result = repository.login(username, password);
        
        // 转发结果到 loginResult
        result.observeForever(user -> {
            loginResult.setValue(user);
            
            if (user == null) {
                errorMessage.setValue("登录失败，请检查用户名和密码");
            }
        });
    }
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     */
    public void register(String username, String password, String email) {
        LiveData<User> result = repository.register(username, password, email);
        
        // 转发结果到 registerResult
        result.observeForever(user -> {
            registerResult.setValue(user);
            
            if (user == null) {
                errorMessage.setValue("注册失败，用户名可能已被使用");
            }
        });
    }
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     */
    public void loadUserById(int userId) {
        currentUser = repository.getUserById(userId);
    }
    
    /**
     * 更新用户信息
     * @param user 用户对象
     */
    public void updateUser(User user) {
        repository.updateUser(user);
    }
    
    /**
     * 获取登录结果
     * @return 登录结果LiveData
     */
    public LiveData<User> getLoginResult() {
        return loginResult;
    }
    
    /**
     * 获取注册结果
     * @return 注册结果LiveData
     */
    public LiveData<User> getRegisterResult() {
        return registerResult;
    }
    
    /**
     * 获取错误消息
     * @return 错误消息LiveData
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * 获取当前用户
     * @return 当前用户LiveData
     */
    public LiveData<UserEntity> getCurrentUser() {
        return currentUser;
    }
} 