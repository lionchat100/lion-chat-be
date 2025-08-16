package com.lion.be.chat.message.service;

import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.MessageMapper;
import com.lion.be.global.config.RabbitMQConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMessagePublisher implements MessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MessageMapper adapter;
    private final MessagePersistence messagePersistence;

    @PostConstruct
    @Override
    public void setUpCallbacks() {
        rabbitTemplate.setReturnsCallback(returned -> {
            try {
                ChatMessageResponse response = (ChatMessageResponse) rabbitTemplate
                        .getMessageConverter()
                        .fromMessage(returned.getMessage());

                log.warn("메시지 라우팅 실패: {}", response);

                ChatMessage chatMessage = adapter.fromResponse(response);
                messagePersistence.updateMessageStatus(chatMessage.getId(), MessageStatus.PENDING);

            } catch (Exception e) {
                log.error("실패한 메시지 처리 중 오류", e);
            }
        });

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("메시지 브로커 도달 성공");
            } else {
                log.warn("메시지 브로커 도달 실패. 원인: {}", cause);
            }
        });
    }

    @Override
    public void publishMessage(ChatMessage message) {
        String routingKey = "chat.message." + message.getChatRoomId();

        ChatMessageResponse response = adapter.toResponse(message, false);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CHAT_EXCHANGE_NAME,
                routingKey,
                response
        );

        log.info("메시지를 발행합니다. RoutingKey: {}, Content: {}", routingKey, message.getContent());
    }
}
