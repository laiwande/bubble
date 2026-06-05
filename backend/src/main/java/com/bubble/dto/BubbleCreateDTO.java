package com.bubble.dto;

import lombok.Data;
import java.util.List;

@Data
public class BubbleCreateDTO {
    private String name;
    private String cardSkin;
    private Integer ageMin;
    private Integer ageMax;
    private String genderRatio;
    private Integer maxMember;
    private String description;

    // 标签字段
    private List<String> allowTags;      // 允许的标签
    private List<String> banTags;        // 禁止的标签
    private List<String> bubbleLabelTags; // Bubble 自身标签
}
