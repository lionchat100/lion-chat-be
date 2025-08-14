package com.lion.be.chat.service;

import com.lion.be.chat.domain.MessageStatus;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.domain.entity.ChatRoomUser;
import com.lion.be.chat.repository.ChatRoomUserRepository;
import com.lion.be.chat.repository.MessageEntityAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageProcessor implements MessageProcessor {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessagePersistence messagePersistence;
    private final MessagePublisher messagePublisher;
    private final MessageEntityAdapter adapter;

    @Override
    public void processIncomingMessage(ChatMessageRequest request, Long senderId) {
        log.info("메시지 요청 들어옴: {}", request);
        log.info("senderId: {}", senderId);

        ChatMessage messageToSave = adapter.fromRequest(request, senderId);
        ChatMessage savedMessage = messagePersistence.saveMessage(messageToSave);
        log.info("채팅 메시지 저장 완료: {}", savedMessage);

        messagePublisher.publishMessage(savedMessage);
        log.info("채팅 메시지 발행 완료: {}", savedMessage);

        messagePersistence.updateMessageStatus(savedMessage.getId(), MessageStatus.DELIVERED);
    }

    /**
     * 메시지 처리 실패 시 PENDING 처리된 메시지들을 재전송 대기 상태로 유지시킵니다.
     *
     * @param message 처리 실패한 채팅 메시지 응답 DTO
     */
    @Override
    public void processFailedMessage(ChatMessageResponse message) {
        log.warn("메시지 처리 실패: {}", message);
        messagePersistence.updateMessageStatus(new ObjectId(message.messageId()), MessageStatus.PENDING);
    }

    private Long determineTargetUser(Long chatRoomId, Long senderId) {
        Set<ChatRoomUser> userSet = chatRoomUserRepository.findById_ChatRoomId(chatRoomId);
        return userSet.stream()
                .filter(chatRoomUser -> !chatRoomUser.getId().getUserId().equals(senderId))
                .map(chatRoomUser -> chatRoomUser.getId().getUserId())
                .findFirst()
                .orElse(null);
    }
}
