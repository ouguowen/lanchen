package com.example.aicreator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.aicreator.R;
import com.example.aicreator.databinding.ActivityMainBinding;
import com.example.aicreator.utils.PreferenceManager;

/**
 * 主活动
 * 提供应用主界面，包含各个功能入口
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        
        // 初始化偏好设置管理器
        preferenceManager = new PreferenceManager(this);
        
        // 检查登录状态
        if (!preferenceManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        // 设置卡片点击监听
        binding.cardTextToImage.setOnClickListener(this);
        binding.cardImageToVideo.setOnClickListener(this);
        binding.cardVoiceClone.setOnClickListener(this);
        binding.cardContentWriting.setOnClickListener(this);
        binding.cardDigitalHuman.setOnClickListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_profile) {
            // 打开个人资料页面
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_my_creations) {
            // 打开我的作品页面
            Toast.makeText(this, "我的作品功能即将上线", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            // 打开设置页面
            Toast.makeText(this, "设置功能即将上线", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_logout) {
            // 执行登出操作
            performLogout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        
        if (viewId == R.id.card_text_to_image) {
            // 打开文生图功能
            Intent intent = new Intent(this, TextToImageActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.card_image_to_video) {
            // 打开图生视频功能
            Intent intent = new Intent(this, ImageToVideoActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.card_voice_clone) {
            // 打开声音克隆功能
            Intent intent = new Intent(this, VoiceCloneActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.card_content_writing) {
            // 打开文案创作功能
            Intent intent = new Intent(this, ContentWritingActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.card_digital_human) {
            // 打开数字人生成功能
            Intent intent = new Intent(this, DigitalHumanActivity.class);
            startActivity(intent);
        }
    }
    
    /**
     * 执行登出操作
     */
    private void performLogout() {
        preferenceManager.clear();
        navigateToLogin();
    }
    
    /**
     * 导航到登录页面
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 