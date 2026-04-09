# Bubble App

## 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Android SDK | API 34 |
| Gradle | ≥ 8.7 |
| MySQL | 8.x |
| Redis | 7.x |

## 部署步骤

### 后端

```bash
cd backend

# 1. 配置 src/main/resources/application.yml（数据库/Redis连接）
# 2. 启动
mvn spring-boot:run
```

默认端口 `8080`。

### 前端

```bash
cd frontend

# 方式一：Android Studio 打开 frontend 目录，点 Run

# 方式二：命令行
./gradlew installDebug
```

APK 输出：`frontend/app/build/outputs/apk/debug/app-debug.apk`

## 前端接口配置

修改 `ApiClient.java` 中的 base URL：

```java
// 模拟器访问宿主机用 10.0.2.2
private static final String BASE_URL = "http://10.0.2.2:8080/api/";
```
