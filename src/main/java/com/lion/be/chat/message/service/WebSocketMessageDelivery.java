package com.lion.be.chat.message.service;

import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketMessageDelivery implements MessageDelivery {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessagePersistence messagePersistence;
    private final MessageMapper adapter;

    @Override
    public boolean deliverToClient(Long userId, ChatMessageResponse message) {
        try {
            String destination = "/topic/chatroom/" + message.chatRoomId();
            messagingTemplate.convertAndSend(destination, message);
            log.info("WebSocket 메시지 전송 성공: userId={}, messageId={}", userId, message.messageId());
            return true;
        } catch (Exception e) {
            log.error("WebSocket 메시지 전송 실패: userId={}, messageId={}, error={}",
                    userId, message.messageId(), e.getMessage());
            return false;
        }
    }

    @Override
    public void deliverPendingMessages(Long userId) {
        List<ChatMessage> pendingMessages = messagePersistence.getPendingMessages(userId);

        for (ChatMessage message : pendingMessages) {
            ChatMessageResponse response = adapter.toResponse(message, false);
            if (deliverToClient(userId, response)) {
                messagePersistence.updateMessageStatus(message.getId(), MessageStatus.DELIVERED);
            }
        }

        log.info("Pending 메시지 {} 건 전송 완료: userId={}", pendingMessages.size(), userId);
    }
}
