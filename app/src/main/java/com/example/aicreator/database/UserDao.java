package com.example.aicreator.database;

import android.util.Log;

import com.example.aicreator.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户数据访问对象
 * 处理用户相关的数据库操作
 */
public class UserDao {
    private static final String TAG = "UserDao";
    
    /**
     * 创建新用户
     * @param user 用户对象
     * @return 创建成功返回true，否则返回false
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO " + DatabaseConfig.TABLE_USERS + 
                " (username, password, email, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            Log.e(TAG, "创建用户失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，如果未找到则返回null
     */
    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM " + DatabaseConfig.TABLE_USERS + " WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
            return null;
        } catch (SQLException e) {
            Log.e(TAG, "查询用户失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象，如果未找到则返回null
     */
    public User findUserById(int id) {
        String sql = "SELECT * FROM " + DatabaseConfig.TABLE_USERS + " WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
            return null;
        } catch (SQLException e) {
            Log.e(TAG, "查询用户失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM " + DatabaseConfig.TABLE_USERS;
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            return users;
        } catch (SQLException e) {
            Log.e(TAG, "获取所有用户失败: " + e.getMessage());
            return users;
        }
    }
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 更新成功返回true，否则返回false
     */
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE " + DatabaseConfig.TABLE_USERS + " SET last_login = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "更新最后登录时间失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新成功返回true，否则返回false
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE " + DatabaseConfig.TABLE_USERS + 
                " SET username = ?, email = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "更新用户信息失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 更新成功返回true，否则返回false
     */
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE " + DatabaseConfig.TABLE_USERS + " SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "更新密码失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM " + DatabaseConfig.TABLE_USERS + " WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "删除用户失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 将ResultSet映射为User对象
     * @param rs ResultSet结果集
     * @return User对象
     * @throws SQLException SQL异常
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
} 