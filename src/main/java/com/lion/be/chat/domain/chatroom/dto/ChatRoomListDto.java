package com.lion.be.chat.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomListDto {
    private Long chatRoomId;

    private String lastChat;

    private LocalDateTime lastChatTime;

    private String opponentName;
    private Long opponentId;

    private Boolean isRead;

    private String userImageUrl;
}
