package com.lion.be.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    private String id;
    private String senderName;
    private Long senderId;

    private LocalDateTime date;
    private String content;
}
