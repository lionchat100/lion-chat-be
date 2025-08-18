package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.room.domain.entity.ChatRoomUser;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import com.lion.be.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final WebSocketMessenger webSocketMessenger;

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE_NAME)
    public void handleMessage(ChatMessageResponse message) {
        log.info("메시지 수신: {}", message);
        Long senderId = message.id();
        Set<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findById_ChatRoomId(message.chatRoomId());
        ChatRoomUser receiver = chatRoomUsers.stream()
                .filter(user -> !user.getId().getUserId().equals(senderId))
                .findFirst()
                .orElse(null);
        webSocketMessenger.deliverToClient(receiver.getId().getUserId(), message);
    }
}
