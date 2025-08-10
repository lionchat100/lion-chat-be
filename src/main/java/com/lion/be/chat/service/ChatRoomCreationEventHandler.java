package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatRoomCreationEvent;
import com.lion.be.chat.domain.dto.ChatRoomCreationResponse;
import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.chat.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomCreationEventHandler {

    private final ChatRoomServiceImpl chatRoomService;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AsyncChatRoomService asyncChatRoomService;

    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitListener(queues = "chatroom.creation.queue")
    @Transactional
    public void handleChatRoomCreation(ChatRoomCreationEvent event) {
        log.info("채팅방 생성 이벤트 수신. EventId: {}, SenderId: {}, ReceiverId: {}",
                event.eventId(), event.senderId(), event.receiverId());

        ChatRoomCreationResponse response;

        try {
            if (processedEvents.contains(event.eventId())) {
                log.warn("이미 처리된 이벤트입니다. EventId: {}", event.eventId());
                response = new ChatRoomCreationResponse(
                        false,
                        "이미 처리된 요청입니다.",
                        null,
                        event.correlationId(),
                        LocalDateTime.now(),
                        "DUPLICATE_REQUEST"
                );
            } else {
                response = createChatRoomSafely(event);
                processedEvents.add(event.eventId());
            }

        } catch (Exception e) {
            log.error("채팅방 생성 처리 중 오류 발생. EventId: {}", event.eventId(), e);
            response = new ChatRoomCreationResponse(
                    false,
                    "채팅방 생성 중 오류가 발생했습니다: " + e.getMessage(),
                    null,
                    event.correlationId(),
                    LocalDateTime.now(),
                    "CREATION_ERROR"
            );
        }

        if (event.replyTo() != null) {
            sendResponse(event.replyTo(), response);
        }

        asyncChatRoomService.handleResponse(response);
    }

    private ChatRoomCreationResponse createChatRoomSafely(ChatRoomCreationEvent event) {
        try {
            Optional<Long> existingChatRoomId = chatRoomUserRepository.findChatRoomIdByTwoUserIds(
                    event.senderId(), event.receiverId());

            if (existingChatRoomId.isPresent()) {
                log.info("채팅방이 이미 존재합니다. ChatRoomId: {}", existingChatRoomId.get());
                return new ChatRoomCreationResponse(
                        true,
                        "기존 채팅방을 사용합니다.",
                        existingChatRoomId.get(),
                        event.correlationId(),
                        LocalDateTime.now(),
                        null
                );
            }

            ChatRoom chatRoom = chatRoomService.findOrCreateChatRoom(
                    event.senderId(), event.receiverId());

            log.info("채팅방 생성 완료. ChatRoomId: {}", chatRoom.getId());

            return new ChatRoomCreationResponse(
                    true,
                    "채팅방이 성공적으로 생성되었습니다.",
                    chatRoom.getId(),
                    event.correlationId(),
                    chatRoom.getRegDt(),
                    null
            );

        } catch (DataIntegrityViolationException e) {
            log.warn("데이터 제약 조건 위반. SenderId: {}, ReceiverId: {}",
                    event.senderId(), event.receiverId(), e);

            Optional<Long> existingChatRoomId = chatRoomUserRepository.findChatRoomIdByTwoUserIds(
                    event.senderId(), event.receiverId());

            if (existingChatRoomId.isPresent()) {
                return new ChatRoomCreationResponse(
                        true,
                        "동시 생성으로 인해 기존 채팅방을 사용합니다.",
                        existingChatRoomId.get(),
                        event.correlationId(),
                        LocalDateTime.now(),
                        null
                );
            } else {
                throw new RuntimeException("채팅방 생성 실패 및 기존 채팅방 조회 실패", e);
            }
        } catch (Exception e) {
            log.error("채팅방 생성 중 예상치 못한 오류 발생", e);
            throw e;
        }
    }

    private void sendResponse(String replyTo, ChatRoomCreationResponse response) {
        try {
            rabbitTemplate.convertAndSend(replyTo, response);
            log.debug("응답 전송 완료. ReplyTo: {}, CorrelationId: {}",
                    replyTo, response.correlationId());
        } catch (Exception e) {
            log.error("응답 전송 실패. ReplyTo: {}", replyTo, e);
        }
    }
}
