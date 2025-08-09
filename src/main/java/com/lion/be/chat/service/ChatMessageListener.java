package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener {

    private final MessageProcessor messageProcessor;

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE_NAME)
    public void handleMessage(ChatMessageResponse message) {
        log.info("메시지 수신: {}", message);
        messageProcessor.processIncomingMessage(message);
    }
}
