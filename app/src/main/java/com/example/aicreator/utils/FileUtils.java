package com.example.aicreator.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件操作工具类
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    
    /**
     * 保存Bitmap到相册
     * @param context 上下文
     * @param bitmap 要保存的Bitmap
     * @param folderName 保存的文件夹名称
     * @return 是否保存成功
     */
    public static boolean saveBitmapToGallery(Context context, Bitmap bitmap, String folderName) {
        String fileName = "AI_Image_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + folderName);
            
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri != null) {
                try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        return true;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Failed to save bitmap: " + e.getMessage());
                }
            }
            return false;
        } else {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName);
            if (!directory.exists() && !directory.mkdirs()) {
                return false;
            }
            
            File file = new File(directory, fileName);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                
                // 添加到媒体库
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Failed to save bitmap: " + e.getMessage());
                return false;
            }
        }
    }
    
    /**
     * 创建临时文件
     * @param context 上下文
     * @param prefix 文件前缀
     * @param suffix 文件后缀
     * @return 临时文件
     */
    public static File createTempFile(Context context, String prefix, String suffix) {
        try {
            return File.createTempFile(prefix, suffix, context.getCacheDir());
        } catch (IOException e) {
            Log.e(TAG, "Failed to create temp file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 清除缓存文件
     * @param context 上下文
     */
    public static void clearCache(Context context) {
        try {
            File cacheDir = context.getCacheDir();
            deleteDir(cacheDir);
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear cache: " + e.getMessage());
        }
    }
    
    /**
     * 递归删除目录
     * @param dir 目录
     * @return 是否删除成功
     */
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir != null && dir.delete();
    }
}