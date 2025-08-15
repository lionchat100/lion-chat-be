package com.lion.be.chat.room.domain.dto;

import java.time.ZonedDateTime;

public record ChatRoomResponse(
        Long chatRoomId,
        String name,
        String lastContent,
        ZonedDateTime lastSendAt,
        String imageUrl,
        boolean isRead
) {
}
