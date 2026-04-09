package com.bubble.dto;

import lombok.Data;

@Data
public class BubbleCreateDTO {
    private String name;
    private String cardSkin;
    private Integer ageMin;
    private Integer ageMax;
    private String genderRatio;
    private Integer maxMember;
    private String description;
}
