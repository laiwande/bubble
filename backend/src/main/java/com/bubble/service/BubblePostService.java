package com.bubble.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bubble.dto.PostCreateDTO;
import com.bubble.entity.BubblePost;

public interface BubblePostService extends IService<BubblePost> {
    BubblePost createPost(PostCreateDTO dto, Long userId);
    IPage<BubblePost> getPostList(Long bubbleId, Page<BubblePost> page);
    void likePost(Long postId, Long userId);
    void commentPost(Long postId, Long userId, String content);
}
