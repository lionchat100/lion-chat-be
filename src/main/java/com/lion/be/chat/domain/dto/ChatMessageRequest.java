package com.lion.be.chat.domain.dto;

public record ChatMessageRequest(
        Long chatRoomId,
        String senderName,
        Long senderId,
        Long receiverId,
        String content
) {}
