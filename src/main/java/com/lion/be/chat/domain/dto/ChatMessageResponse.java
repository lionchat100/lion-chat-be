package com.lion.be.chat.domain.dto;

import java.time.ZonedDateTime;

public record ChatMessageResponse(
        String messageId,
        Long chatRoomId,
        Long senderId,
        ZonedDateTime createdAt,
        String content,
        boolean isEnd
) {}