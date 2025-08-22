package com.lion.be.chat.room.domain.dto;

import java.time.ZonedDateTime;

public record ChatRoomInitResponse(
        Long chatRoomId,
        ZonedDateTime lastSendAt
) {
    public static ChatRoomInitResponse toResponse(
            Long chatRoomId,
            ZonedDateTime lastSendAt
    ) {
        return new ChatRoomInitResponse(
                chatRoomId,
                lastSendAt
        );
    }
}
