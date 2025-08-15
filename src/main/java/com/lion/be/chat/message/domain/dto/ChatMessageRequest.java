package com.lion.be.chat.message.domain.dto;

public record ChatMessageRequest(
        Long chatRoomId,
        String content
) {}
