package com.bubble.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubble.dto.MessageSendDTO;
import com.bubble.entity.Conversation;
import com.bubble.entity.Message;
import com.bubble.entity.MessageRead;
import com.bubble.mapper.ConversationMapper;
import com.bubble.mapper.MessageMapper;
import com.bubble.mapper.MessageReadMapper;
import com.bubble.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageReadMapper messageReadMapper;

    @Override
    @Transactional
    public Message sendMessage(MessageSendDTO dto, Long userId) {
        Message message = new Message();
        BeanUtils.copyProperties(dto, message);
        message.setSenderId(userId);
        save(message);
        return message;
    }

    @Override
    public IPage<Message> getMessageList(Long conversationId, Page<Message> page) {
        return getBaseMapper().selectMessagesWithSender(page, conversationId);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return 0;
    }

    @Override
    public void markAsRead(Long messageId, Long userId) {
        MessageRead read = new MessageRead();
        read.setMessageId(messageId);
        read.setUserId(userId);
        messageReadMapper.insert(read);
    }
}
