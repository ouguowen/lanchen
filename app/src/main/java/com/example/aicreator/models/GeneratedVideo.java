package com.example.aicreator.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * 生成视频模型类
 * 用于存储AI生成的视频信息
 */
@Entity(tableName = "generated_videos")
public class GeneratedVideo implements Parcelable {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String videoPath;  // 视频本地路径
    private String sourceImagePath;  // 源图片路径
    private String videoStyle;  // 视频风格
    private int duration;  // 视频时长(秒)
    private int motionIntensity;  // 运动强度(0-100)
    private Date createdAt;  // 创建时间
    private boolean isFavorite;  // 是否收藏
    private long userId;  // 用户ID
    
    public GeneratedVideo() {
        this.createdAt = new Date();
        this.isFavorite = false;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getVideoPath() {
        return videoPath;
    }
    
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
    
    public String getSourceImagePath() {
        return sourceImagePath;
    }
    
    public void setSourceImagePath(String sourceImagePath) {
        this.sourceImagePath = sourceImagePath;
    }
    
    public String getVideoStyle() {
        return videoStyle;
    }
    
    public void setVideoStyle(String videoStyle) {
        this.videoStyle = videoStyle;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public int getMotionIntensity() {
        return motionIntensity;
    }
    
    public void setMotionIntensity(int motionIntensity) {
        this.motionIntensity = motionIntensity;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    public long getUserId() {
        return userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    // Parcelable实现
    protected GeneratedVideo(Parcel in) {
        id = in.readInt();
        videoPath = in.readString();
        sourceImagePath = in.readString();
        videoStyle = in.readString();
        duration = in.readInt();
        motionIntensity = in.readInt();
        long tmpCreatedAt = in.readLong();
        createdAt = tmpCreatedAt != -1 ? new Date(tmpCreatedAt) : null;
        isFavorite = in.readByte() != 0;
        userId = in.readLong();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(videoPath);
        dest.writeString(sourceImagePath);
        dest.writeString(videoStyle);
        dest.writeInt(duration);
        dest.writeInt(motionIntensity);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeLong(userId);
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    public static final Creator<GeneratedVideo> CREATOR = new Creator<GeneratedVideo>() {
        @Override
        public GeneratedVideo createFromParcel(Parcel in) {
            return new GeneratedVideo(in);
        }
        
        @Override
        public GeneratedVideo[] newArray(int size) {
            return new GeneratedVideo[size];
        }
    };
} 