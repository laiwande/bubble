package com.bubble.dto;

import lombok.Data;

@Data
public class MessageSendDTO {
    private Long conversationId;
    private String content;
    private String msgType;
    private String extra;
}
