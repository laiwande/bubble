# Bubble App 开发文档

## 项目概述
Bubble 是一个社交社区应用，包含用户管理、Bubble 群组、泡泡墙帖子、聊天等功能。

## 技术栈

### 后端
- Spring Boot 3.2.0
- MySQL 8.x
- Redis 7.x
- MyBatis Plus 3.5.4
- JWT

### 前端
- Android SDK
- Java
- Retrofit2
- Gson
- Glide

## 数据库设计

### 用户模块
1. `user` - 用户表
2. `user_label` - 用户标签表

### Bubble 社区模块
3. `bubble` - Bubble 群组表
4. `bubble_member` - Bubble 成员表

### Bubble 标签模块
5. `label` - 标签表
6. `bubble_label` - Bubble 标签关联表

### 泡泡墙模块
7. `bubble_post` - 泡泡墙帖子表
8. `post_comment` - 评论表
9. `post_like` - 点赞表

### 聊天模块
10. `conversation` - 会话表
11. `message` - 消息表
12. `message_read` - 消息已读表

## 后端 API 接口

### 用户相关
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/info` - 更新用户信息

### Bubble 相关
- `POST /api/bubble/create` - 创建 Bubble
- `GET /api/bubble/list` - 获取 Bubble 列表
- `GET /api/bubble/{id}` - 获取 Bubble 详情
- `POST /api/bubble/{id}/join` - 加入 Bubble
- `POST /api/bubble/{id}/leave` - 离开 Bubble

### 帖子相关
- `POST /api/post/create` - 创建帖子
- `GET /api/post/list/{bubbleId}` - 获取帖子列表
- `POST /api/post/{postId}/like` - 点赞帖子
- `POST /api/post/{postId}/comment` - 评论帖子

### 消息相关
- `POST /api/message/send` - 发送消息
- `GET /api/message/list/{conversationId}` - 获取消息列表
- `GET /api/message/unread` - 获取未读消息数
- `POST /api/message/{messageId}/read` - 标记消息已读

## 快速开始

### 后端
1. 配置 `application.yml` 中的数据库和 Redis 连接
2. 执行 `sql/init.sql` 初始化数据库
3. 运行 `mvn spring-boot:run` 启动后端服务

### 前端
1. 配置 `ApiClient.java` 中的 BASE_URL
2. 使用 Android Studio 打开项目
3. 点击运行按钮启动应用

## 项目结构

```
bubble/
├── backend/
│   ├── src/main/java/com/bubble/
│   │   ├── entity/        # 实体类
│   │   ├── mapper/        # MyBatis Mapper
│   │   ├── service/       # 服务层
│   │   ├── controller/    # 控制器
│   │   ├── dto/           # 数据传输对象
│   │   ├── dvo/           # 数据视图对象
│   │   ├── config/        # 配置类
│   │   ├── util/          # 工具类
│   │   └── interceptor/   # 拦截器
│   └── sql/               # 数据库脚本
└── frontend/
    └── app/src/main/
        ├── java/com/bubble/
        │   ├── ui/        # Activity
        │   ├── model/     # 数据模型
        │   ├── network/   # 网络请求
        │   └── utils/     # 工具类
        └── res/           # 资源文件
```

## 注意事项

1. 后端使用 JWT 进行身份验证，除了登录和注册接口外，其他接口都需要在请求头中携带 `Authorization: Bearer {token}`
2. Android 端使用 SharedPreferences 存储 token
3. 数据库密码和 JWT secret 应在生产环境中修改
4. 前端的 BASE_URL 需要根据实际后端服务地址进行修改
