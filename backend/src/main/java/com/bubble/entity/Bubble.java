package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bubble")
public class Bubble {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long creatorId;

    private String cardSkin;

    private Integer ageMin;

    private Integer ageMax;

    private String genderRatio;

    private Integer maxMember;

    private Integer currentMember;

    private String description;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
