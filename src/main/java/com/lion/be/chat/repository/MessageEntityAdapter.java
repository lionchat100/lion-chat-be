package com.lion.be.chat.repository;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEntityAdapter {

    private final UserRepository userRepository;
    private final UserRepository userRoomRepository;

    public ChatMessage fromRequest(ChatMessageRequest request, Long senderId) {
        User sender = userRepository.findById(senderId);
        log.info("sender={}", sender);
        return new ChatMessage(
                senderId,
                sender.getName(),
                request.chatRoomId(),
                ZonedDateTime.now(),
                request.content(),
                false,
                MessageStatus.DELIVERED
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
                MessageStatus.DELIVERED
        );
    }

    public ChatMessageResponse toResponse(ChatMessage message, boolean isEnd) {
        User sender = userRoomRepository.findById(message.getSenderId());
        log.info("sender={}", sender);
        return new ChatMessageResponse(
                message.getId() != null ? message.getId().toString() : new ObjectId().toString(),
                message.getChatRoomId(),
                message.getSenderId(),
                sender.getName(),
                sender.getImageUrl() != null ? sender.getImageUrl() : "",
                message.getCreatedAt(),
                message.getContent(),
                isEnd
        );
    }
}
