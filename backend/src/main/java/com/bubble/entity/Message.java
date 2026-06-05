package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long conversationId;

    private Long senderId;

    private String content;

    private String msgType;

    private String extra;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 从selectMessagesWithSender JOIN查询填充，非数据库字段
    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String avatar;
}
