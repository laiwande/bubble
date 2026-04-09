package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post_comment")
public class PostComment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private Long userId;

    private String content;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
