package com.lion.be.chat.domain.chatroom.dto;

import java.time.LocalDateTime;

public record LastMessageInfo(
        Long roomId,
        String lastChat,
        LocalDateTime lastChatTime
) {}
