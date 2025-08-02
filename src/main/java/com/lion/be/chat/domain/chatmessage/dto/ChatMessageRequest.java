package com.lion.be.chat.domain.chatmessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ChatMessageRequest {
    private Long chatRoomId;
    private String content;
    private LocalDateTime date;
}
