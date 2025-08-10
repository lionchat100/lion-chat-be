package com.lion.be.chat.repository;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class MessageEntityAdapter {

    public ChatMessage fromRequest(ChatMessageRequest request) {
        return new ChatMessage(
                request.senderId(),
                request.senderName(),
                request.chatRoomId(),
                Instant.now(),
                request.content(),
                false,
                MessageStatus.SENT
        );
    }

    public ChatMessage fromResponse(ChatMessageResponse response) {
        return new ChatMessage(
                new ObjectId(response.messageId()),
                response.senderId(),
                response.senderName(),
                response.chatRoomId(),
                response.timestamp().toInstant(),
                response.content(),
                false,
                MessageStatus.SENT
        );
    }

    public ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId() != null ? message.getId().toString() : new ObjectId().toString(),
                message.getChatRoomId(),
                message.getSenderName(),
                message.getSenderId(),
                message.getDate().atZone(ZoneId.of("Asia/Seoul")),
                message.getContent()
        );
    }
}
