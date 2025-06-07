package com.example.aicreator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.aicreator.R;
import com.example.aicreator.databinding.ActivityRegisterBinding;
import com.example.aicreator.utils.PreferenceManager;
import com.example.aicreator.viewmodels.UserViewModel;

/**
 * 注册活动
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserViewModel userViewModel;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 初始化ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // 初始化偏好设置管理器
        preferenceManager = new PreferenceManager(this);

        // 设置注册按钮点击监听
        binding.btnRegister.setOnClickListener(v -> registerUser());

        // 设置登录文本点击监听
        binding.tvLoginPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // 观察注册状态
        userViewModel.getRegisterResult().observe(this, user -> {
            binding.progressBar.setVisibility(View.GONE);
            
            if (user != null) {
                // 注册成功，保存用户信息
                preferenceManager.setLoggedIn(true);
                preferenceManager.setUserId(user.getId());
                preferenceManager.setUsername(user.getUsername());
                
                // 显示成功消息
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                
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
     * 注册用户
     */
    private void registerUser() {
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // 验证输入
        if (username.isEmpty()) {
            binding.tilUsername.setError("请输入用户名");
            return;
        } else {
            binding.tilUsername.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("请输入有效的邮箱地址");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            binding.tilPassword.setError("密码至少需要6个字符");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError("两次密码输入不一致");
            return;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        // 显示进度条
        binding.progressBar.setVisibility(View.VISIBLE);

        // 调用ViewModel进行注册
        userViewModel.register(username, password, email);
    }

    /**
     * 导航到主界面
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
} 