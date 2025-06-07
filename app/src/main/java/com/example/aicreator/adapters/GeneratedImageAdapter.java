package com.example.aicreator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aicreator.R;
import com.example.aicreator.database.entity.GeneratedImageEntity;

import java.util.List;

/**
 * 生成图像适配器
 * 用于显示历史生成的图像
 */
public class GeneratedImageAdapter extends RecyclerView.Adapter<GeneratedImageAdapter.ImageViewHolder> {

    private final Context context;
    private List<GeneratedImageEntity> images;
    private final OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(GeneratedImageEntity image);
        void onDeleteClick(GeneratedImageEntity image);
    }

    public GeneratedImageAdapter(Context context, List<GeneratedImageEntity> images, OnImageClickListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_generated_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        GeneratedImageEntity image = images.get(position);
        
        // 加载图像
        Glide.with(context)
                .load(image.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(holder.imageView);
        
        // 设置收藏状态
        if (image.isFavorite()) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }
        
        // 设置点击监听
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageClick(image);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(image);
            }
        });
        
        holder.favoriteButton.setOnClickListener(v -> {
            // 切换收藏状态
            boolean newState = !image.isFavorite();
            image.setFavorite(newState);
            holder.favoriteButton.setImageResource(newState ? 
                    R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            
            // 回调到活动中更新数据库
            if (listener != null) {
                listener.onImageClick(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
    
    /**
     * 更新图像列表
     * @param newImages 新的图像列表
     */
    public void updateImages(List<GeneratedImageEntity> newImages) {
        this.images = newImages;
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton favoriteButton;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_generated_image);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
} 