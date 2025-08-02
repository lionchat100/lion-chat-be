package com.lion.be.chat.domain.chatmessage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageDto {

    private String id;
    private String senderName;
    private Long senderId;

    private LocalDateTime date;
    private String content;
}
