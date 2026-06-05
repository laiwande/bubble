package com.bubble.vo;

import com.bubble.entity.Bubble;
import com.bubble.entity.BubblePost;
import lombok.Data;

import java.util.List;

@Data
public class BubbleDetailVO {
    private Bubble bubble;
    private List<BubblePost> recentPosts;
}
