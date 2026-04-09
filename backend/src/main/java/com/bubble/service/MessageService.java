package com.bubble.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bubble.dto.MessageSendDTO;
import com.bubble.entity.Message;

public interface MessageService extends IService<Message> {
    Message sendMessage(MessageSendDTO dto, Long userId);
    IPage<Message> getMessageList(Long conversationId, Page<Message> page);
    Integer getUnreadCount(Long userId);
    void markAsRead(Long messageId, Long userId);
}
