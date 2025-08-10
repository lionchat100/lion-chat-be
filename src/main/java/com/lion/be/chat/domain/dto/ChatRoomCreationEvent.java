package com.lion.be.chat.domain.dto;

import java.time.LocalDateTime;

public record ChatRoomCreationEvent(
        String eventId,
        Long senderId,
        Long receiverId,
        String senderName,
        String messageContent,
        LocalDateTime requestTime,
        String correlationId,
        String replyTo
) {
}
