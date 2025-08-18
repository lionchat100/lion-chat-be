package com.lion.be.chat.room.domain.dto;

import java.time.ZonedDateTime;

public record ChatRoomResponse(
        Long chatRoomId,
        String nickname, // sender nickname
        String lastContent,
        ZonedDateTime lastSendAt,
        String imageUrl, // sender image url
        boolean isRead
) {
}
