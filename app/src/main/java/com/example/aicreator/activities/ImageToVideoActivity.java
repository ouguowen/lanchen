package com.example.aicreator.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.aicreator.R;
import com.example.aicreator.adapters.GeneratedVideoAdapter;
import com.example.aicreator.databinding.ActivityImageToVideoBinding;
import com.example.aicreator.models.GeneratedVideo;
import com.example.aicreator.viewmodel.ImageToVideoViewModel;

import java.io.File;
import java.util.ArrayList;

/**
 * 图生视频功能页面
 * 允许用户选择图片，设置参数，生成和保存视频
 */
public class ImageToVideoActivity extends AppCompatActivity {
    
    private ActivityImageToVideoBinding binding;
    private ImageToVideoViewModel viewModel;
    private GeneratedVideoAdapter videoAdapter;
    
    // 视频风格列表
    private final String[] videoStyles = {"自然流畅", "电影镜头", "慢动作", "快速转场", "梦幻效果"};
    
    // 图片选择器
    private final ActivityResultLauncher<String> imagePickerLauncher = 
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    showSelectedImage(uri);
                    viewModel.setSelectedImageUri(uri);
                }
            });
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化视图绑定
        binding = ActivityImageToVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(ImageToVideoViewModel.class);
        
        // 设置工具栏
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // 初始化视频风格选择器
        setupStyleSpinner();
        
        // 设置进度条监听器
        setupSeekBars();
        
        // 设置点击事件
        setupClickListeners();
        
        // 初始化视频列表
        setupRecyclerView();
        
        // 观察ViewModel状态
        observeViewModel();
    }
    
    /**
     * 设置视频风格选择器
     */
    private void setupStyleSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, videoStyles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerStyle.setAdapter(adapter);
        binding.spinnerStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 保存选择的风格
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 不做任何操作
            }
        });
    }
    
    /**
     * 设置进度条监听器
     */
    private void setupSeekBars() {
        // 视频时长进度条
        binding.seekbarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.tvDurationValue.setText(progress + "秒");
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // 运动强度进度条
        binding.seekbarMotion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String intensityText;
                if (progress < 30) {
                    intensityText = "低";
                } else if (progress < 70) {
                    intensityText = "中等";
                } else {
                    intensityText = "高";
                }
                binding.tvMotionValue.setText(intensityText);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    /**
     * 设置点击事件
     */
    private void setupClickListeners() {
        // 选择图片卡片点击事件
        binding.cardSelectImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
        
        // 生成按钮点击事件
        binding.btnGenerate.setOnClickListener(v -> {
            generateVideo();
        });
        
        // 保存按钮点击事件
        binding.btnSave.setOnClickListener(v -> {
            Uri videoUri = viewModel.getGeneratedVideoUri().getValue();
            if (videoUri != null) {
                viewModel.saveVideoToGallery(videoUri);
            }
        });
        
        // 分享按钮点击事件
        binding.btnShare.setOnClickListener(v -> {
            shareVideo();
        });
    }
    
    /**
     * 初始化视频列表
     */
    private void setupRecyclerView() {
        videoAdapter = new GeneratedVideoAdapter(new ArrayList<>());
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerHistory.setAdapter(videoAdapter);
        
        // 设置点击事件
        videoAdapter.setOnItemClickListener(video -> {
            // 点击历史视频项
            playVideo(video);
        });
    }
    
    /**
     * 观察ViewModel状态
     */
    private void observeViewModel() {
        // 观察加载状态
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        // 观察错误消息
        viewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        
        // 观察成功消息
        viewModel.getSuccessMessage().observe(this, successMsg -> {
            if (successMsg != null && !successMsg.isEmpty()) {
                Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
            }
        });
        
        // 观察选择的图片
        viewModel.getSelectedImageFile().observe(this, imageFile -> {
            if (imageFile != null) {
                Glide.with(this)
                        .load(imageFile)
                        .into(binding.ivSelectedImage);
                binding.tvSelectImage.setVisibility(View.GONE);
            }
        });
        
        // 观察生成的视频
        viewModel.getGeneratedVideoUri().observe(this, videoUri -> {
            if (videoUri != null) {
                binding.cardResult.setVisibility(View.VISIBLE);
                binding.videoView.setVideoURI(videoUri);
                binding.videoView.start();
            }
        });
        
        // 观察用户视频列表
        viewModel.getUserVideos().observe(this, videos -> {
            if (videos != null) {
                videoAdapter.updateVideos(videos);
            }
        });
    }
    
    /**
     * 显示选中的图片
     * @param uri 图片URI
     */
    private void showSelectedImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.ivSelectedImage);
        binding.tvSelectImage.setVisibility(View.GONE);
    }
    
    /**
     * 生成视频
     */
    private void generateVideo() {
        String style = videoStyles[binding.spinnerStyle.getSelectedItemPosition()];
        int duration = binding.seekbarDuration.getProgress();
        int motionIntensity = binding.seekbarMotion.getProgress();
        
        viewModel.generateVideo(style, duration, motionIntensity);
    }
    
    /**
     * 播放视频
     * @param video 视频对象
     */
    private void playVideo(GeneratedVideo video) {
        if (video != null && video.getVideoPath() != null) {
            File videoFile = new File(video.getVideoPath());
            if (videoFile.exists()) {
                Uri videoUri = Uri.fromFile(videoFile);
                binding.cardResult.setVisibility(View.VISIBLE);
                binding.videoView.setVideoURI(videoUri);
                binding.videoView.start();
            }
        }
    }
    
    /**
     * 分享视频
     */
    private void shareVideo() {
        Uri videoUri = viewModel.getGeneratedVideoUri().getValue();
        if (videoUri != null) {
            Uri contentUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider",
                    new File(videoUri.getPath()));
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/mp4");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            startActivity(Intent.createChooser(shareIntent, "分享视频"));
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.videoView.stopPlayback();
    }
}