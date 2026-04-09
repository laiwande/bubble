package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message_read")
public class MessageRead {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long messageId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime readTime;
}
