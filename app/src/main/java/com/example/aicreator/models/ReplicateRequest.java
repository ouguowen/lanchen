package com.example.aicreator.models;

import java.util.Map;

/**
 * Replicate API请求模型类
 * 用于构建请求Replicate API的数据结构
 */
public class ReplicateRequest {
    private String version;
    private Map<String, Object> input;
    private boolean stream;
    
    public ReplicateRequest() {
        this.stream = false;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Map<String, Object> getInput() {
        return input;
    }
    
    public void setInput(Map<String, Object> input) {
        this.input = input;
    }
    
    public boolean isStream() {
        return stream;
    }
    
    public void setStream(boolean stream) {
        this.stream = stream;
    }
} 