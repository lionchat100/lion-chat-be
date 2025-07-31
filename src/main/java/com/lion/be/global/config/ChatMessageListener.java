package com.lion.be.global.config;

import com.lion.be.chat.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageListener {

    private final SimpMessageSendingOperations messagingTemplate;

    // RabbitMQ의 'anonymousQueue'를 리스닝합니다.
    // SpEL을 사용하여 `RabbitMQConfig`에서 생성된 익명 큐의 실제 이름을 참조합니다.
    @RabbitListener(queues = "#{anonymousQueue.name}")
    public void receiveMessage(ChatMessage chatMessage) {
        // 받은 메시지를 WebSocket의 "/topic/public"을 구독하는 클라이언트들에게 보냅니다.
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

}
