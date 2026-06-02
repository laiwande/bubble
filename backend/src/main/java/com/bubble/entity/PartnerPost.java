package com.bubble.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("partner_post")
public class PartnerPost {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String topicName;

    private String address;

    private String activityDate;

    private Integer partnerNumber;

    private String description;

    private String wishTags;

    private String banTags;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
