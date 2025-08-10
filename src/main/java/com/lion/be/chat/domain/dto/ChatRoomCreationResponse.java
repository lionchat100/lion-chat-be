package com.lion.be.chat.domain.dto;

import java.time.LocalDateTime;

public record ChatRoomCreationResponse(
        boolean success,
        String message,
        Long chatRoomId,
        String correlationId,
        LocalDateTime createdAt,
        String errorCode
) {
}
