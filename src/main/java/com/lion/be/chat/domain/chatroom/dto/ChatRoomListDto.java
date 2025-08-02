package com.lion.be.chat.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomListDto {
    private Long id;

    private LocalDateTime regDt;

    private String recentMessageContent;

    private LocalDateTime recentMessageDt;

    private String opponentName;
    private Long opponentId;

    private Boolean isRead;
}
