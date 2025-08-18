package com.lion.be.chat.message.domain.dto;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.user.domain.entity.User;
import org.bson.types.ObjectId;

import java.time.ZonedDateTime;

public record ChatMessageResponse(
        String messageId,
        Long chatRoomId,
        Long id, // sender id
        String nickname, // sender nickname
        String imageUrl, // sender image url
        ZonedDateTime createdAt,
        String content,
        boolean isEnd
) {
    public static ChatMessage fromResponse(ChatMessageResponse response, User sender) {
        return new ChatMessage(
                response.id(),
                sender.getNickname(),
                response.chatRoomId(),
                response.createdAt(),
                response.content(),
                false,
                MessageStatus.DELIVERED
        );
    }

    public static ChatMessageResponse toResponse(ChatMessage message, User sender, String imageUrl, boolean isEnd) {
        return new ChatMessageResponse(
                message.getId() != null ? message.getId().toString() : new ObjectId().toString(),
                message.getChatRoomId(),
                message.getSenderId(),
                sender.getNickname(),
                imageUrl,
                message.getCreatedAt(),
                message.getContent(),
                isEnd
        );
    }
}