package com.lion.be.chat.domain.dto;

public record ChatMessageRequest(
        Long chatRoomId,
        String content
) {}
