package com.lion.be.chat.domain.dto;

import java.time.ZonedDateTime;

public record ChatMessageResponse(
        String messageId,
        Long chatRoomId,
        Long senderId,
        String senderName,
        String senderImageUrl,
        ZonedDateTime createdAt,
        String content,
        boolean isEnd
) {}