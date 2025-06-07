package com.example.aicreator.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aicreator.database.entity.UserEntity;

import java.util.List;

/**
 * 用户数据访问对象
 * 定义用户表的数据库操作
 */
@Dao
public interface UserDao {
    
    /**
     * 插入用户
     * @param user 用户实体
     * @return 插入的ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserEntity user);
    
    /**
     * 更新用户
     * @param user 用户实体
     */
    @Update
    void update(UserEntity user);
    
    /**
     * 删除用户
     * @param user 用户实体
     */
    @Delete
    void delete(UserEntity user);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    @Query("SELECT * FROM users WHERE id = :id")
    LiveData<UserEntity> getUserById(int id);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户实体
     */
    @Query("SELECT * FROM users WHERE username = :username")
    LiveData<UserEntity> getUserByUsername(String username);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    @Query("SELECT * FROM users")
    LiveData<List<UserEntity>> getAllUsers();
    
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 用户实体，如果验证失败则为null
     */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    UserEntity login(String username, String password);
    
    /**
     * 更新最后登录时间
     * @param userId 用户ID
     * @param lastLoginTime 最后登录时间
     */
    @Query("UPDATE users SET last_login = :lastLoginTime WHERE id = :userId")
    void updateLastLoginTime(int userId, long lastLoginTime);
} 