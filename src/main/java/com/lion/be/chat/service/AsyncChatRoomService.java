package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatRoomCreationEvent;
import com.lion.be.chat.domain.dto.ChatRoomCreationResponse;
import com.lion.be.global.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncChatRoomService {

    private final RabbitTemplate rabbitTemplate;

    private final ConcurrentHashMap<String, CompletableFuture<ChatRoomCreationResponse>> pendingRequests
            = new ConcurrentHashMap<>();

    public CompletableFuture<ChatRoomCreationResponse> createChatRoomAsync(ChatMessageRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();

        CompletableFuture<ChatRoomCreationResponse> responseFuture = new CompletableFuture<>();
        pendingRequests.put(correlationId, responseFuture);

        ChatRoomCreationEvent event = new ChatRoomCreationEvent(
                eventId,
                request.senderId(),
                request.receiverId(),
                request.senderName(),
                request.content(),
                LocalDateTime.now(),
                correlationId,
                "chatroom.creation.response.queue"
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CHAT_EXCHANGE_NAME,
                    "chatroom.create",
                    event
            );

            log.info("채팅방 생성 이벤트 전송. EventId: {}, SenderId: {}, ReceiverId: {}",
                    eventId, request.senderId(), request.receiverId());

        } catch (Exception e) {
            log.error("채팅방 생성 이벤트 전송 실패", e);
            pendingRequests.remove(correlationId);
            responseFuture.completeExceptionally(e);
        }

        return responseFuture;
    }

    public void handleResponse(ChatRoomCreationResponse response) {
        String correlationId = response.correlationId();
        CompletableFuture<ChatRoomCreationResponse> future = pendingRequests.remove(correlationId);

        if (future != null) {
            future.complete(response);
            log.info("채팅방 생성 응답 처리 완료. CorrelationId: {}", correlationId);
        } else {
            log.warn("해당하는 요청을 찾을 수 없습니다. CorrelationId: {}", correlationId);
        }
    }
}
