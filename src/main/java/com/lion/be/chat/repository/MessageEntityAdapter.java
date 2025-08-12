package com.lion.be.chat.repository;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class MessageEntityAdapter {

    private final UserRepository userRoomRepository;

    public ChatMessage fromRequest(ChatMessageRequest request) {
        return new ChatMessage(
                request.senderId(),
                request.chatRoomId(),
                ZonedDateTime.now(),
                request.content(),
                false,
                MessageStatus.SENT
        );
    }

    public ChatMessage fromResponse(ChatMessageResponse response) {
        return new ChatMessage(
                new ObjectId(response.messageId()),
                response.senderId(),
                userRoomRepository.findById(response.senderId()).getName(),
                response.chatRoomId(),
                response.createdAt(),
                response.content(),
                false,
                MessageStatus.SENT
        );
    }

    public ChatMessageResponse toResponse(ChatMessage message, boolean isEnd) {
        return new ChatMessageResponse(
                message.getId() != null ? message.getId().toString() : new ObjectId().toString(),
                message.getChatRoomId(),
                message.getSenderId(),
                message.getCreatedAt(),
                message.getContent(),
                isEnd
        );
    }
}
