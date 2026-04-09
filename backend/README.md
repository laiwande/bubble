# Bubble Backend 后端服务

## 技术栈
- Spring Boot 3.2.0
- MySQL 8.x
- Redis 7.x
- MyBatis Plus 3.5.4
- JWT

## 项目结构
```
backend/
├── src/main/java/com/bubble/
│   ├── BubbleApplication.java      # 启动类
│   ├── common/                     # 通用模块
│   ├── config/                     # 配置类
│   ├── controller/                 # 控制器
│   ├── service/                    # 服务层
│   ├── mapper/                     # 数据访问层
│   └── entity/                     # 实体类
├── src/main/resources/
│   ├── application.yml             # 配置文件
│   └── mapper/                     # MyBatis XML 映射文件
└── sql/
    └── init.sql                    # 数据库初始化脚本
```

## 快速开始

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.6+

### 2. 数据库初始化
```bash
mysql -u root -p < sql/init.sql
```

### 3. 配置文件
修改 `application.yml` 中的数据库和 Redis 连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bubble_db
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 启动服务
```bash
mvn clean install
mvn spring-boot:run
```

### 5. 测试接口
访问 http://localhost:8080/api/test/hello
