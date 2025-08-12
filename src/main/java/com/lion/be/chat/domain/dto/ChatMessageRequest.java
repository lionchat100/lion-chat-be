package com.lion.be.chat.domain.dto;

public record ChatMessageRequest(
        Long chatRoomId,
        Long senderId, // todo 이걸 stomp, security 레벨에서 처리
        Long receiverId,
        String content
) {}
