package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bubble_member")
public class BubbleMember {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bubbleId;

    private Long userId;

    private String role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinTime;
}
