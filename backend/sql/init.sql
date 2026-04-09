-- Bubble App 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS bubble_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bubble_db;

-- ==================== 一、用户模块 ====================

-- 1. 用户表
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  nickname VARCHAR(50) COMMENT '昵称',
  avatar VARCHAR(255) COMMENT '头像',
  gender TINYINT COMMENT '性别：0未知 1男 2女',
  age INT COMMENT '年龄',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  bio TEXT COMMENT '个人简介',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_username (username),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户标签表
CREATE TABLE user_label (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  label_name VARCHAR(50) NOT NULL COMMENT '标签名称',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签表';

-- ==================== 二、Bubble 社区模块 ====================

-- 3. Bubble 群组表
CREATE TABLE bubble (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '群组ID',
  name VARCHAR(100) NOT NULL COMMENT '群组名称',
  creator_id BIGINT NOT NULL COMMENT '创建者ID',
  card_skin VARCHAR(100) COMMENT '卡片皮肤',
  age_min INT COMMENT '最小年龄',
  age_max INT COMMENT '最大年龄',
  gender_ratio VARCHAR(20) COMMENT '性别比例',
  max_member INT DEFAULT 100 COMMENT '最大成员数',
  current_member INT DEFAULT 0 COMMENT '当前成员数',
  description TEXT COMMENT '群组描述',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bubble群组表';

-- 4. Bubble 成员表
CREATE TABLE bubble_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  bubble_id BIGINT NOT NULL COMMENT '群组ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role VARCHAR(20) NOT NULL COMMENT '角色：owner/admin/member',
  join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  UNIQUE KEY uk_bubble_user (bubble_id, user_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bubble成员表';

-- ==================== 三、Bubble 标签模块 ====================

-- 5. 标签表
CREATE TABLE label (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
  name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 6. Bubble 标签表
CREATE TABLE bubble_label (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  bubble_id BIGINT NOT NULL COMMENT '群组ID',
  label_id BIGINT NOT NULL COMMENT '标签ID',
  type VARCHAR(20) NOT NULL COMMENT '类型：allow允许/ban禁止',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_bubble_id (bubble_id),
  INDEX idx_label_id (label_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bubble标签关联表';

-- ==================== 四、泡泡墙模块 ====================

-- 7. 泡泡墙帖子表
CREATE TABLE bubble_post (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
  bubble_id BIGINT NOT NULL COMMENT '群组ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  content TEXT NOT NULL COMMENT '内容',
  images JSON COMMENT '图片列表',
  like_count INT DEFAULT 0 COMMENT '点赞数',
  comment_count INT DEFAULT 0 COMMENT '评论数',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_bubble_id (bubble_id),
  INDEX idx_user_id (user_id),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='泡泡墙帖子表';

-- 8. 评论表
CREATE TABLE post_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  content TEXT NOT NULL COMMENT '评论内容',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_post_id (post_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 9. 点赞表
CREATE TABLE post_like (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_post_user (post_id, user_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ==================== 五、聊天模块 ====================

-- 10. 会话表
CREATE TABLE conversation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
  type VARCHAR(20) NOT NULL COMMENT '类型：private私聊/bubble群聊',
  target_id BIGINT NOT NULL COMMENT '目标ID（私聊为对方用户ID，群聊为群组ID）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_type_target (type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- 11. 消息表
CREATE TABLE message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  conversation_id BIGINT NOT NULL COMMENT '会话ID',
  sender_id BIGINT NOT NULL COMMENT '发送者ID',
  content TEXT COMMENT '消息内容',
  msg_type VARCHAR(20) NOT NULL COMMENT '消息类型：text/image/voice',
  extra JSON COMMENT '额外信息（图片URL、语音时长等）',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除：0未删除 1已删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_conversation_id (conversation_id),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 12. 消息已读表
CREATE TABLE message_read (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  message_id BIGINT NOT NULL COMMENT '消息ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  read_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '已读时间',
  UNIQUE KEY uk_message_user (message_id, user_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息已读表';

-- ==================== 初始化数据 ====================

-- 初始化标签
INSERT INTO label (name) VALUES
('音乐'), ('运动'), ('电影'), ('旅游'), ('动漫'),
('游戏'), ('摄影'), ('美食'), ('读书'), ('科技');
