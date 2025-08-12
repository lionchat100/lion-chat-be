package com.lion.be.chat.domain.dto;

import java.time.Instant;

public record ChatRoomCreationEvent(
        Long user1Id,
        Long user2Id,
        Instant requestTime
) {
}
