package com.example.aicreator.database.util;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * 日期转换器
 * 用于Room数据库Date类型与Long类型的相互转换
 */
public class DateConverter {
    
    /**
     * 将Date转换为Long类型(时间戳)
     * @param date 日期
     * @return 时间戳，如果date为null则返回null
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
    
    /**
     * 将Long类型(时间戳)转换为Date
     * @param timestamp 时间戳
     * @return 日期，如果timestamp为null则返回null
     */
    @TypeConverter
    public static Date timestampToDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
} 