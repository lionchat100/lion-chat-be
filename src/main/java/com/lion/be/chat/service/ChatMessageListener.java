package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatRoomUser;
import com.lion.be.chat.repository.ChatRoomUserRepository;
import com.lion.be.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageDelivery messageDelivery;
    private final MessagePersistence messagePersistence;

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE_NAME)
    public void handleMessage(ChatMessageResponse message) {
        log.info("메시지 수신: {}", message);
        Long senderId = message.senderId();
        Set<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findById_ChatRoomId(message.chatRoomId());
        ChatRoomUser receiver = chatRoomUsers.stream()
                .filter(user -> !user.getId().getUserId().equals(senderId))
                .findFirst()
                .orElse(null);
        messageDelivery.deliverToClient(receiver.getId().getUserId(), message);
    }
}
