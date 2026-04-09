package com.bubble.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubble.common.result.Result;
import com.bubble.dto.MessageSendDTO;
import com.bubble.entity.Message;
import com.bubble.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Result<Message> sendMessage(@RequestBody MessageSendDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Message message = messageService.sendMessage(dto, userId);
        return Result.success(message);
    }

    @GetMapping("/list/{conversationId}")
    public Result<IPage<Message>> getMessageList(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<Message> pageParam = new Page<>(page, size);
        IPage<Message> result = messageService.getMessageList(conversationId, pageParam);
        return Result.success(result);
    }

    @GetMapping("/unread")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PostMapping("/{messageId}/read")
    public Result<Void> markAsRead(@PathVariable Long messageId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        messageService.markAsRead(messageId, userId);
        return Result.success();
    }
}
