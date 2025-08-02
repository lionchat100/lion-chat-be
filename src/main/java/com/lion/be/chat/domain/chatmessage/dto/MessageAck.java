package com.lion.be.chat.domain.chatmessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class MessageAck {
    private Long chatRoomId;
    private Long senderId;
    private LocalDateTime date;
}
