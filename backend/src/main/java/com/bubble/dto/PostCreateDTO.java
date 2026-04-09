package com.bubble.dto;

import lombok.Data;

@Data
public class PostCreateDTO {
    private Long bubbleId;
    private String content;
    private String images;
}
