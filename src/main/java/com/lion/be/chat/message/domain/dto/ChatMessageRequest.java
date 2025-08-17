package com.lion.be.chat.message.domain.dto;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.user.domain.entity.User;
import jakarta.validation.constraints.NotBlank;

import java.time.ZonedDateTime;

public record ChatMessageRequest(
        @NotBlank Long chatRoomId,
        @NotBlank String content
) {
    public static ChatMessage fromRequest(ChatMessageRequest request, User sender) {
        return new ChatMessage(
                sender.getId(),
                sender.getName(),
                request.chatRoomId(),
                ZonedDateTime.now(),
                request.content(),
                false,
                MessageStatus.PENDING
        );
    }
}
