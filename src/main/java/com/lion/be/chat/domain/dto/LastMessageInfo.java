package com.lion.be.chat.domain.dto;

import java.time.LocalDateTime;

public record LastMessageInfo(
        Long roomId,
        String lastChat,
        LocalDateTime lastChatTime
) {}
