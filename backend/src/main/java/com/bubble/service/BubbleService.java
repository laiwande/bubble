package com.bubble.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bubble.dto.BubbleCreateDTO;
import com.bubble.entity.Bubble;

public interface BubbleService extends IService<Bubble> {
    Bubble createBubble(BubbleCreateDTO dto, Long userId);
    IPage<Bubble> getBubbleList(Page<Bubble> page);
    Bubble getBubbleDetail(Long id);
    void joinBubble(Long bubbleId, Long userId);
    void leaveBubble(Long bubbleId, Long userId);
}
