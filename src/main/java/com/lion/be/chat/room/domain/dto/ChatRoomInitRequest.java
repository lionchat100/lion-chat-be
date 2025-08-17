package com.lion.be.chat.room.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRoomInitRequest(
        @NotBlank Long receiverId
) {
}
