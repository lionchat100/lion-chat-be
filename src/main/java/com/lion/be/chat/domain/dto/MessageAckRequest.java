package com.lion.be.chat.domain.dto;

public record MessageAckRequest(
        String messageId,
        Long userId
) {
}
