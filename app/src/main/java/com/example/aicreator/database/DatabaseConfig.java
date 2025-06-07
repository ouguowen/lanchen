package com.example.aicreator.database;

/**
 * 数据库配置类
 * 存储MySQL数据库连接的相关配置信息
 */
public class DatabaseConfig {
    // 数据库连接信息
    public static final String DB_HOST = "test-db-mysql.ns-twk4hi58.svc";
    public static final int DB_PORT = 3306;
    public static final String DB_NAME = "test-db";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "ivylmt29";
    
    // JDBC连接URL
    public static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME +
            "?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    
    // 连接超时设置（毫秒）
    public static final int CONNECTION_TIMEOUT = 5000;
    
    // 数据库表名
    public static final String TABLE_USERS = "users";
    public static final String TABLE_GENERATED_IMAGES = "generated_images";
    public static final String TABLE_GENERATED_VIDEOS = "generated_videos";
    public static final String TABLE_GENERATED_CONTENT = "generated_content";
    public static final String TABLE_USER_PREFERENCES = "user_preferences";
    
    // 最大连接数
    public static final int MAX_POOL_SIZE = 10;
} 