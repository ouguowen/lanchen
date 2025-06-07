# AI创作助手 Android应用

## 项目介绍
AI创作助手是一个多功能AI创作工具，集成了文生图、图生视频、声音克隆、文案创作和数字人生成等功能。

## 环境要求
- Android Studio Meerkat | 2024.3.2 或更高版本
- JDK 17
- Gradle 8.11.1
- Android SDK 34
- 至少2GB的可用磁盘空间

## 配置说明
1. 项目使用Gradle 8.11.1和Android Gradle插件8.1.0
2. SDK路径需要根据实际环境在local.properties中配置
3. API密钥需在local.properties中配置，包括：
   - replicate.api.key - 用于文生图、图生视频和数字人生成
   - huggingface.api.key - 用于文案创作和声音克隆

## 项目结构
- activities: 包含所有界面活动
- adapters: 数据适配器
- viewmodels: 视图模型，处理业务逻辑
- models: 数据模型
- database: 数据库访问层(Room)
- utils: 工具类
- services: API服务接口
- fragments: UI片段

## 支持功能
- 用户注册登录
- 文生图(AI绘画)
- 图生视频
- 声音克隆
- 文案创作
- 数字人生成

## 注意事项
项目使用MVVM架构，结合Retrofit和Room实现数据处理和API调用。所有AI功能都通过第三方API实现，需要配置对应的API密钥才能正常使用。 