package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_label")
public class UserLabel {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String labelName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
