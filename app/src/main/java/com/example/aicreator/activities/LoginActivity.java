package com.example.aicreator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.aicreator.R;
import com.example.aicreator.databinding.ActivityLoginBinding;
import com.example.aicreator.utils.PreferenceManager;
import com.example.aicreator.viewmodels.UserViewModel;

/**
 * 登录活动
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserViewModel userViewModel;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // 初始化偏好设置管理器
        preferenceManager = new PreferenceManager(this);
        
        // 检查是否已登录
        if (preferenceManager.isLoggedIn()) {
            navigateToMainActivity();
            finish();
            return;
        }

        // 设置登录按钮点击监听
        binding.btnLogin.setOnClickListener(v -> loginUser());

        // 设置注册文本点击监听
        binding.tvRegisterPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 设置忘记密码文本点击监听
        binding.tvForgotPassword.setOnClickListener(v -> {
            // TODO: 实现忘记密码功能
            Toast.makeText(this, "忘记密码功能待实现", Toast.LENGTH_SHORT).show();
        });

        // 观察登录状态
        userViewModel.getLoginResult().observe(this, user -> {
            binding.progressBar.setVisibility(View.GONE);
            
            if (user != null) {
                // 登录成功，保存用户信息
                preferenceManager.setLoggedIn(true);
                preferenceManager.setUserId(user.getId());
                preferenceManager.setUsername(user.getUsername());
                
                // 导航到主界面
                navigateToMainActivity();
                finish();
            }
        });

        // 观察错误消息
        userViewModel.getErrorMessage().observe(this, errorMsg -> {
            binding.progressBar.setVisibility(View.GONE);
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 登录用户
     */
    private void loginUser() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // 验证输入
        if (username.isEmpty()) {
            binding.tilUsername.setError("请输入用户名");
            return;
        } else {
            binding.tilUsername.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("请输入密码");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        // 显示进度条
        binding.progressBar.setVisibility(View.VISIBLE);

        // 调用ViewModel进行登录
        userViewModel.login(username, password);
    }

    /**
     * 导航到主界面
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
} 