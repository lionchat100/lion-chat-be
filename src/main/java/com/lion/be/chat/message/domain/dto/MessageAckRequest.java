package com.lion.be.chat.message.domain.dto;

public record MessageAckRequest(
        String messageId,
        Long userId
) {
}
