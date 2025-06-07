package com.example.aicreator.models;

import java.util.List;
import java.util.Map;

/**
 * Replicate API响应模型类
 * 用于解析Replicate API的返回数据
 */
public class ReplicateResponse {
    private String id;
    private String version;
    private List<String> output;
    private String status;
    private Map<String, Object> input;
    private String error;
    private String createdAt;
    private String completedAt;
    private Map<String, Object> metrics;
    private String[] logs;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public List<String> getOutput() {
        return output;
    }
    
    public void setOutput(List<String> output) {
        this.output = output;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Map<String, Object> getInput() {
        return input;
    }
    
    public void setInput(Map<String, Object> input) {
        this.input = input;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
    
    public Map<String, Object> getMetrics() {
        return metrics;
    }
    
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }
    
    public String[] getLogs() {
        return logs;
    }
    
    public void setLogs(String[] logs) {
        this.logs = logs;
    }
    
    /**
     * 检查响应是否成功
     * @return 如果状态为succeeded返回true
     */
    public boolean isSuccessful() {
        return "succeeded".equals(status);
    }
    
    /**
     * 检查响应是否仍在处理中
     * @return 如果状态为starting或processing返回true
     */
    public boolean isProcessing() {
        return "starting".equals(status) || "processing".equals(status);
    }
} 