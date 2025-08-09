package com.lion.be.chat.service;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.repository.MessageEntityAdapter;
import com.lion.be.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeadLetterQueueListener {

    private final MessagePersistence messagePersistence;
    private final MessageEntityAdapter adapter;

    @RabbitListener(queues = RabbitMQConfig.CHATROOM_QUEUE_NAME)
    public void handleDeadLetterMessage(ChatMessageResponse message) {
        log.warn("DLQ에서 메시지 수신: {}", message);

        ChatMessage chatMessage = adapter.fromResponse(message);
        messagePersistence.updateMessageStatus(chatMessage.getId(), MessageStatus.PENDING);

        log.info("메시지 상태를 PENDING으로 변경: messageId={}", message.messageId());
    }
}
