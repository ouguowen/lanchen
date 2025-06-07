package com.example.aicreator.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aicreator.R;
import com.example.aicreator.databinding.ItemVideoBinding;
import com.example.aicreator.models.GeneratedVideo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 生成视频列表适配器
 * 用于在RecyclerView中显示生成的视频列表
 */
public class GeneratedVideoAdapter extends RecyclerView.Adapter<GeneratedVideoAdapter.VideoViewHolder> {
    
    private List<GeneratedVideo> videos;
    private OnItemClickListener listener;
    
    /**
     * 构造方法
     * @param videos 视频列表
     */
    public GeneratedVideoAdapter(List<GeneratedVideo> videos) {
        this.videos = videos;
    }
    
    /**
     * 更新视频列表
     * @param videos 新的视频列表
     */
    public void updateVideos(List<GeneratedVideo> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }
    
    /**
     * 设置项点击监听器
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = ItemVideoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VideoViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        GeneratedVideo video = videos.get(position);
        holder.bind(video, listener);
    }
    
    @Override
    public int getItemCount() {
        return videos != null ? videos.size() : 0;
    }
    
    /**
     * 视频ViewHolder
     */
    static class VideoViewHolder extends RecyclerView.ViewHolder {
        private final ItemVideoBinding binding;
        
        public VideoViewHolder(ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        /**
         * 绑定数据
         * @param video 视频对象
         * @param listener 点击监听器
         */
        public void bind(GeneratedVideo video, OnItemClickListener listener) {
            // 设置视频元数据
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            binding.tvVideoDate.setText(dateFormat.format(video.getCreatedAt()));
            binding.tvVideoStyle.setText(video.getVideoStyle());
            binding.tvVideoDuration.setText(video.getDuration() + "秒");
            
            // 设置视频缩略图
            if (video.getSourceImagePath() != null) {
                // 使用源图片作为缩略图
                Glide.with(binding.getRoot().getContext())
                        .load(new File(video.getSourceImagePath()))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(binding.ivVideoThumbnail);
            } else if (video.getVideoPath() != null) {
                // 使用视频第一帧作为缩略图
                Glide.with(binding.getRoot().getContext())
                        .load(Uri.fromFile(new File(video.getVideoPath())))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(binding.ivVideoThumbnail);
            }
            
            // 设置收藏状态
            binding.ivFavorite.setImageResource(
                    video.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            
            // 设置点击事件
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(video);
                }
            });
        }
    }
    
    /**
     * 项点击监听器接口
     */
    public interface OnItemClickListener {
        void onItemClick(GeneratedVideo video);
    }
} 