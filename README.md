# 🫧 Bubble 社交社区

该项目作为上海电力大学信管软件开发课设，是一个基于兴趣群组的社交社区 Android 应用，所有界面设计皆为原创

项目后端基于 Spring Boot + MyBatis Plus 构建，前端采用 Android 原生开发（Java），通过 RESTful API 实现前后端交互

## ✨ 功能介绍

### 👤 用户模块
- 登录系统（邮箱 + 密码，JWT Token 认证）
- 注册账号（用户名 + 邮箱 + 密码）
- 查看和编辑个人资料（昵称、头像、性别、年龄、简介等）

### 💬 Bubble 群组
- 创建 Bubble（设置名称、描述、年龄范围、性别比例、最大成员数等）
- 浏览 Bubble 列表（分页展示）
- 查看 Bubble 详情
- 加入 / 离开 Bubble
- Bubble 成员角色：**owner（拥有者）** / **admin（管理员）** / **member（成员）**

### 📄 泡泡墙帖子
- 在 Bubble 内发布帖子（支持文字 + 图片）
- 浏览帖子列表（分页展示）
- 点赞帖子
- 评论帖子

### 💬 消息聊天
- 发送消息（支持 text / image / voice 三种类型）
- 私聊会话（private）
- 群聊会话（bubble）
- 获取未读消息数量
- 标记消息已读

### 🏷️ 标签系统
- 预置 10 个兴趣标签（音乐、运动、电影、旅游、动漫、游戏、摄影、美食、读书、科技）
-Bubble 可关联标签（支持允许/禁止类型）

## 🚀 部署指南

### 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Android SDK | API 34 |
| Gradle | ≥ 8.7 |
| MySQL | 8.x |
| Redis | 7.x |

### 数据库配置

`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bubble_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 修改为你的密码

data:
  redis:
    host: localhost
    port: 6379
    password:         # 如有密码请填写
```

首次启动前需执行 `backend/sql/init.sql` 初始化数据库表结构

### 后端部署

```bash
git clone https://github.com/your-username/bubble.git
cd backend
mvn spring-boot:run
```

默认端口 `8080`，API 前缀为 `/api`

### 前端部署

```bash
cd frontend

# 方式一：Android Studio 打开 frontend 目录，点 Run

# 方式二：命令行
./gradlew installDebug
```

APK 输出路径：`frontend/app/build/outputs/apk/debug/app-debug.apk`

### 前端接口配置

修改 `frontend/app/src/main/java/com/bubble/network/ApiClient.java` 中的 base URL：

```java
// 模拟器访问宿主机用 10.0.2.2，真机请改为实际 IP
private static final String BASE_URL = "http://10.0.2.2:8080/api/";
```

## 🔑 默认账号

后端首次启动时会自动创建管理员账号：

| 用户名 | 密码 | 邮箱 |
|--------|------|------|
| `admin` | `admin123` | `admin@bubble.com` |
