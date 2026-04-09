package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bubble_label")
public class BubbleLabel {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bubbleId;

    private Long labelId;

    private String type;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
