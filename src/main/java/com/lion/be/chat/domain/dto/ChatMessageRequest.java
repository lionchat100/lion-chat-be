package com.lion.be.chat.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatMessageRequest {

    private Long chatRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime date;

}
