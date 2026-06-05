package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

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

    // === 非数据库字段（由查询时动态填充） ===

    @TableField(exist = false)
    private Integer messageCount;

    @TableField(exist = false)
    private String lastMessage;

    @TableField(exist = false)
    private List<String> labels;
}
