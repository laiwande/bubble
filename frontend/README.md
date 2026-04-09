# Bubble Android App

## 技术栈
- Android SDK
- Java
- Kotlin
- Retrofit2
- Gson
- Glide
- Lifecycle

## 项目结构
```
frontend/
├── app/
│   ├── src/main/java/com/bubble/
│   │   ├── MainActivity.java           # 主 Activity
│   │   ├── api/                        # API 接口
│   │   ├── model/                      # 数据模型
│   │   ├── ui/                         # UI 组件
│   │   ├── utils/                      # 工具类
│   │   └── network/                    # 网络请求
│   ├── src/main/res/
│   │   ├── layout/                     # 布局文件
│   │   ├── values/                     # 资源文件
│   │   └── drawable/                   # 图片资源
│   └── build.gradle                    # 模块级 build.gradle
└── build.gradle                        # 项目级 build.gradle
```

## 快速开始

### 1. 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK API 24+
- Gradle 8.0+

### 2. 配置后端地址
修改 `network/ApiClient.java` 中的 BASE_URL：
```java
private static final String BASE_URL = "http://192.168.1.100:8080/api/";
```

### 3. 运行应用
使用 Android Studio 打开项目，点击运行按钮或使用快捷键 `Shift + F10`

## 开发说明
- 网络请求使用 Retrofit2
- 图片加载使用 Glide
- 响应式编程使用 Lifecycle
