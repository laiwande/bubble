package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bubble_post")
public class BubblePost {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bubbleId;

    private Long userId;

    private String content;

    private String images;

    private Integer likeCount;

    private Integer commentCount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
