package com.example.aicreator;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import androidx.room.Room;

import com.example.aicreator.database.AppDatabase;

/**
 * 应用程序类
 * 用于应用程序初始化和全局配置
 */
public class AICreatorApplication extends Application {
    private static final String TAG = "AICreatorApplication";
    private static AICreatorApplication instance;
    private AppDatabase database;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // 在调试版本中启用严格模式，检测主线程IO操作等问题
        if (BuildConfig.DEBUG) {
            enableStrictMode();
        }
        
        // 初始化数据库
        initDatabase();
        
        // 初始化应用全局组件
        initializeComponents();
        
        Log.d(TAG, "应用程序初始化完成");
    }
    
    /**
     * 获取应用程序实例
     */
    public static AICreatorApplication getInstance() {
        return instance;
    }
    
    /**
     * 获取数据库实例
     */
    public AppDatabase getDatabase() {
        return database;
    }
    
    /**
     * 启用严格模式
     */
    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
                
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());
    }
    
    /**
     * 初始化应用全局组件
     */
    private void initializeComponents() {
        // 初始化第三方库等组件
        // 例如: Timber日志库、Firebase等
    }
    
    /**
     * 初始化数据库
     */
    private void initDatabase() {
        database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "aicreator.db")
                .fallbackToDestructiveMigration()
                .build();
        
        Log.d(TAG, "数据库初始化成功");
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "应用程序终止");
        
        // Room数据库会自动关闭连接，不需要手动关闭
    }
} 