package com.example.aicreator.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.aicreator.R;
import com.example.aicreator.adapters.GeneratedImageAdapter;
import com.example.aicreator.database.entity.GeneratedImageEntity;
import com.example.aicreator.databinding.ActivityTextToImageBinding;
import com.example.aicreator.utils.PreferenceManager;
import com.example.aicreator.viewmodels.TextToImageViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 文生图活动
 * 处理文字描述转图像功能
 */
public class TextToImageActivity extends AppCompatActivity implements GeneratedImageAdapter.OnImageClickListener {

    private ActivityTextToImageBinding binding;
    private TextToImageViewModel viewModel;
    private PreferenceManager preferenceManager;
    private GeneratedImageAdapter imageAdapter;
    
    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final String[] IMAGE_SIZES = {"512x512", "768x768", "1024x1024", "1024x1536", "1536x1024"};
    
    private String selectedSize = IMAGE_SIZES[2]; // 默认1024x1024
    private float creativityLevel = 7.0f; // 默认创意度
    private Bitmap currentGeneratedBitmap;
    private GeneratedImageEntity currentImage;
    private boolean isFavorite = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextToImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(TextToImageViewModel.class);
        
        // 初始化偏好设置管理器
        preferenceManager = new PreferenceManager(this);
        
        // 设置用户ID
        viewModel.setCurrentUserId(preferenceManager.getUserId());
        
        // 初始化UI
        setupUI();
        
        // 设置观察者
        observeViewModel();
    }
    
    private void setupUI() {
        // 设置尺寸下拉菜单
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, IMAGE_SIZES);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSize.setAdapter(sizeAdapter);
        binding.spinnerSize.setSelection(2); // 默认选择1024x1024
        
        binding.spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = IMAGE_SIZES[position];
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        // 设置创意度滑块
        binding.seekbarCreativity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                creativityLevel = progress / 10.0f;
                binding.tvCreativityValue.setText(String.format(Locale.getDefault(), "%.1f", creativityLevel));
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        
        // 设置生成按钮点击监听
        binding.btnGenerate.setOnClickListener(v -> generateImage());
        
        // 设置保存按钮点击监听
        binding.btnSave.setOnClickListener(v -> saveImage());
        
        // 设置分享按钮点击监听
        binding.btnShare.setOnClickListener(v -> shareImage());
        
        // 设置收藏按钮点击监听
        binding.btnFavorite.setOnClickListener(v -> toggleFavorite());
        
        // 设置历史记录RecyclerView
        binding.recyclerHistory.setLayoutManager(new GridLayoutManager(this, 2));
        imageAdapter = new GeneratedImageAdapter(this, new ArrayList<>(), this);
        binding.recyclerHistory.setAdapter(imageAdapter);
    }
    
    private void observeViewModel() {
        // 观察生成的图像
        viewModel.getGeneratedImage().observe(this, image -> {
            if (image != null) {
                currentImage = new GeneratedImageEntity(
                        preferenceManager.getUserId(),
                        binding.etPrompt.getText().toString(),
                        image.getImageUrl()
                );
                
                // 加载图像并显示
                Glide.with(this)
                        .asBitmap()
                        .load(image.getImageUrl())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                currentGeneratedBitmap = resource;
                                binding.ivGeneratedImage.setImageBitmap(resource);
                                binding.cardResult.setVisibility(View.VISIBLE);
                                binding.loadingOverlay.setVisibility(View.GONE);
                            }
                            
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        });
        
        // 观察加载状态
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.loadingOverlay.setVisibility(View.VISIBLE);
            } else {
                binding.loadingOverlay.setVisibility(View.GONE);
            }
        });
        
        // 观察错误消息
        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        
        // 观察用户图像历史
        viewModel.getUserImages().observe(this, images -> {
            if (images != null) {
                imageAdapter.updateImages(images);
            }
        });
    }
    
    private void generateImage() {
        String prompt = binding.etPrompt.getText().toString().trim();
        if (prompt.isEmpty()) {
            Toast.makeText(this, "请输入图像描述", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 隐藏键盘
        binding.etPrompt.clearFocus();
        
        // 调用ViewModel生成图像
        viewModel.generateImage(prompt, selectedSize, creativityLevel);
    }
    
    private void saveImage() {
        if (currentGeneratedBitmap == null) {
            return;
        }
        
        // 检查存储权限
        if (checkStoragePermission()) {
            saveImageToStorage();
        } else {
            requestStoragePermission();
        }
    }
    
    private void shareImage() {
        if (currentGeneratedBitmap == null) {
            return;
        }
        
        // 保存临时文件并分享
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File imageFile = new File(cachePath, "shared_image.png");
            
            FileOutputStream stream = new FileOutputStream(imageFile);
            currentGeneratedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            
            Uri imageUri = FileProvider.getUriForFile(this, 
                    getPackageName() + ".fileprovider", imageFile);
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "分享图像"));
            
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "分享失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void toggleFavorite() {
        if (currentImage != null) {
            isFavorite = !isFavorite;
            if (isFavorite) {
                binding.btnFavorite.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
                binding.btnFavorite.setText(R.string.unfavorite);
            } else {
                binding.btnFavorite.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border));
                binding.btnFavorite.setText(R.string.favorite);
            }
            
            // 更新收藏状态
            viewModel.updateFavoriteStatus(currentImage.getId(), isFavorite);
        }
    }
    
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10及以上使用MediaStore API
            return true;
        }
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToStorage();
            } else {
                Toast.makeText(this, "需要存储权限才能保存图像", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void saveImageToStorage() {
        String fileName = "AI_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date()) + ".png";
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10及以上使用MediaStore API
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AICreator");
            
            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(imageUri)) {
                    if (outputStream != null) {
                        currentGeneratedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        Toast.makeText(this, R.string.success_saved, Toast.LENGTH_SHORT).show();
                        
                        // 更新本地路径
                        if (currentImage != null) {
                            currentImage.setLocalPath(imageUri.toString());
                            // 更新数据库
                            // viewModel.updateLocalPath(currentImage.getId(), imageUri.toString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Android 9及以下直接保存到文件
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "AICreator");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            File file = new File(directory, fileName);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                currentGeneratedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, R.string.success_saved, Toast.LENGTH_SHORT).show();
                
                // 更新本地路径
                if (currentImage != null) {
                    currentImage.setLocalPath(file.getAbsolutePath());
                    // 更新数据库
                    // viewModel.updateLocalPath(currentImage.getId(), file.getAbsolutePath());
                }
                
                // 通知图库更新
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(file));
                sendBroadcast(mediaScanIntent);
                
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onImageClick(GeneratedImageEntity image) {
        // 点击历史图像，显示大图
        binding.etPrompt.setText(image.getPrompt());
        
        // 加载图像
        Glide.with(this)
                .asBitmap()
                .load(image.getImageUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        currentGeneratedBitmap = resource;
                        binding.ivGeneratedImage.setImageBitmap(resource);
                        binding.cardResult.setVisibility(View.VISIBLE);
                        
                        // 更新当前图像
                        currentImage = image;
                        isFavorite = image.isFavorite();
                        
                        // 更新收藏按钮状态
                        if (isFavorite) {
                            binding.btnFavorite.setIcon(ContextCompat.getDrawable(TextToImageActivity.this, 
                                    R.drawable.ic_favorite));
                            binding.btnFavorite.setText(R.string.unfavorite);
                        } else {
                            binding.btnFavorite.setIcon(ContextCompat.getDrawable(TextToImageActivity.this, 
                                    R.drawable.ic_favorite_border));
                            binding.btnFavorite.setText(R.string.favorite);
                        }
                    }
                    
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
    
    @Override
    public void onDeleteClick(GeneratedImageEntity image) {
        // 删除图像
        viewModel.deleteImage(image.getId());
        Toast.makeText(this, "图像已删除", Toast.LENGTH_SHORT).show();
        
        // 如果删除的是当前显示的图像，则隐藏结果卡片
        if (currentImage != null && currentImage.getId() == image.getId()) {
            binding.cardResult.setVisibility(View.GONE);
            currentImage = null;
            currentGeneratedBitmap = null;
        }
    }
} 