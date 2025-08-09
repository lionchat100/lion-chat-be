package com.lion.be.chat.domain.dto;

import java.time.ZonedDateTime;

public record ChatMessageResponse(
        String messageId,
        Long chatRoomId,
        String senderName,
        Long senderId,
        ZonedDateTime timestamp,
        String content
) {}
