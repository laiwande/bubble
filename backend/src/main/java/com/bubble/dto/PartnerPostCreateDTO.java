package com.bubble.dto;

import lombok.Data;

@Data
public class PartnerPostCreateDTO {
    private String topicName;       // 活动名称
    private String address;         // 活动地址
    private String activityDate;    // 活动日期 (yyyy-MM-dd)
    private Integer partnerNumber;  // 搭子人数（0=不限）
    private String description;     // 活动描述
    private String wishTags;        // 期望标签 (JSON数组字符串)
    private String banTags;         // 避免标签 (JSON数组字符串)
}
