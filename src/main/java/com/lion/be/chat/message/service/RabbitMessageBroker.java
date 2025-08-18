package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.ChatMessageRepository;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.global.config.RabbitMQConfig;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMessageBroker implements MessageBroker {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @PostConstruct
    @Override
    public void setUpCallbacks() {
        rabbitTemplate.setReturnsCallback(returned -> {
            try {
                ChatMessageResponse response = (ChatMessageResponse) rabbitTemplate
                        .getMessageConverter()
                        .fromMessage(returned.getMessage());

                log.warn("메시지 라우팅 실패: {}", response);

                ChatMessage chatMessage = ChatMessageResponse.fromResponse(response, userRepository.findById(response.id()));
                chatMessage.updateMessageStatus(MessageStatus.PENDING);
                chatMessageRepository.save(chatMessage);

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
        User sender = userRepository.findById(message.getSenderId());
        String imageUrl;

        if (!sender.getUserPhotos().isEmpty()) {
            imageUrl = sender.getUserPhotos().get(0).getImageUrl();
        } else {
            imageUrl = null;
        }
        ChatMessageResponse response = ChatMessageResponse.toResponse(message, sender, imageUrl, false);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CHAT_EXCHANGE_NAME,
                routingKey,
                response
        );

        log.info("메시지를 발행합니다. RoutingKey: {}, Content: {}", routingKey, message.getContent());
    }
}
