package com.example.aicreator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aicreator.R;
import com.example.aicreator.databinding.ActivitySplashBinding;
import com.example.aicreator.utils.PreferenceManager;

/**
 * 启动页活动
 * 显示应用启动画面并初始化必要的组件
 */
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private static final long SPLASH_DISPLAY_TIME = 2000; // 2秒
    private ImageView logoImageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 初始化视图
        logoImageView = binding.ivLogo;
        progressBar = binding.progressBar;
        
        // 设置淡入动画
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        
        binding.ivLogo.startAnimation(fadeIn);
        binding.tvAppName.startAnimation(fadeIn);
        
        // 延迟跳转
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DISPLAY_TIME);
    }
    
    /**
     * 根据登录状态导航到下一个页面
     */
    private void navigateToNextScreen() {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        
        Intent intent;
        if (preferenceManager.isLoggedIn()) {
            // 已登录，跳转到主页
            intent = new Intent(this, MainActivity.class);
        } else {
            // 未登录，跳转到登录页
            intent = new Intent(this, LoginActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // 设置全屏沉浸式体验
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}}
