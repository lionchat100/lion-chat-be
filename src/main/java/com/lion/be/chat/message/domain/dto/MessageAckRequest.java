package com.lion.be.chat.message.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageAckRequest(
        @NotBlank String messageId,
        @NotBlank Long id // sender id
) {
}
