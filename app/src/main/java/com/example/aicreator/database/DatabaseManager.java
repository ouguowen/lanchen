package com.example.aicreator.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * 数据库管理器
 * 负责管理MySQL数据库连接和执行数据库操作
 */
public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private static DatabaseManager instance;
    private final ExecutorService executorService;
    
    private DatabaseManager() {
        // 初始化线程池，用于异步执行数据库操作
        executorService = Executors.newFixedThreadPool(5);
        
        // 初始化数据库
        initDatabase();
    }
    
    /**
     * 获取DatabaseManager单例实例
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * 初始化数据库，创建必要的表
     */
    private void initDatabase() {
        executorService.execute(() -> {
            try {
                // 加载MySQL JDBC驱动
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // 创建必要的表
                createTablesIfNotExist();
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "MySQL JDBC驱动加载失败: " + e.getMessage());
            }
        });
    }
    
    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.JDBC_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD
        );
    }
    
    /**
     * 创建必要的数据库表
     */
    private void createTablesIfNotExist() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 创建用户表
            String createUsersTable = "CREATE TABLE IF NOT EXISTS " + DatabaseConfig.TABLE_USERS + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) UNIQUE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "last_login TIMESTAMP NULL" +
                    ")";
            stmt.execute(createUsersTable);
            
            // 创建生成图像表
            String createImagesTable = "CREATE TABLE IF NOT EXISTS " + DatabaseConfig.TABLE_GENERATED_IMAGES + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "prompt TEXT NOT NULL, " +
                    "image_url VARCHAR(255), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES " + DatabaseConfig.TABLE_USERS + "(id)" +
                    ")";
            stmt.execute(createImagesTable);
            
            // 创建生成视频表
            String createVideosTable = "CREATE TABLE IF NOT EXISTS " + DatabaseConfig.TABLE_GENERATED_VIDEOS + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "source_image_url VARCHAR(255), " +
                    "video_url VARCHAR(255), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES " + DatabaseConfig.TABLE_USERS + "(id)" +
                    ")";
            stmt.execute(createVideosTable);
            
            // 创建生成文案表
            String createContentTable = "CREATE TABLE IF NOT EXISTS " + DatabaseConfig.TABLE_GENERATED_CONTENT + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT, " +
                    "prompt TEXT NOT NULL, " +
                    "content TEXT, " +
                    "content_type VARCHAR(50), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES " + DatabaseConfig.TABLE_USERS + "(id)" +
                    ")";
            stmt.execute(createContentTable);
            
            // 创建用户偏好设置表
            String createPreferencesTable = "CREATE TABLE IF NOT EXISTS " + DatabaseConfig.TABLE_USER_PREFERENCES + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT UNIQUE, " +
                    "theme VARCHAR(20) DEFAULT 'light', " +
                    "notification_enabled BOOLEAN DEFAULT TRUE, " +
                    "language VARCHAR(10) DEFAULT 'zh-CN', " +
                    "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES " + DatabaseConfig.TABLE_USERS + "(id)" +
                    ")";
            stmt.execute(createPreferencesTable);
            
            Log.d(TAG, "数据库表创建成功");
            
        } catch (SQLException e) {
            Log.e(TAG, "创建数据库表失败: " + e.getMessage());
        }
    }
    
    /**
     * 关闭数据库管理器
     */
    public void close() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    /**
     * 执行查询操作
     * @param sql SQL查询语句
     * @param callback 查询结果回调接口
     */
    public void executeQuery(String sql, QueryCallback callback) {
        executorService.execute(() -> {
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                callback.onSuccess(rs);
                
            } catch (SQLException e) {
                Log.e(TAG, "查询执行失败: " + e.getMessage());
                callback.onError(e);
            }
        });
    }
    
    /**
     * 执行更新操作（插入、更新、删除）
     * @param sql SQL更新语句
     * @param callback 更新结果回调接口
     */
    public void executeUpdate(String sql, UpdateCallback callback) {
        executorService.execute(() -> {
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                
                int affectedRows = stmt.executeUpdate(sql);
                callback.onSuccess(affectedRows);
                
            } catch (SQLException e) {
                Log.e(TAG, "更新执行失败: " + e.getMessage());
                callback.onError(e);
            }
        });
    }
    
    /**
     * 查询回调接口
     */
    public interface QueryCallback {
        void onSuccess(ResultSet resultSet) throws SQLException;
        void onError(Exception e);
    }
    
    /**
     * 更新回调接口
     */
    public interface UpdateCallback {
        void onSuccess(int affectedRows);
        void onError(Exception e);
    }
} 