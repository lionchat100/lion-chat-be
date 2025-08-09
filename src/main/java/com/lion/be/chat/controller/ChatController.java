package com.lion.be.chat.controller;

import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.repository.ChatRoomRepository;
import com.lion.be.chat.repository.MessageEntityAdapter;
import com.lion.be.chat.service.MessagePersistence;
import com.lion.be.chat.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final MessagePublisher messagePublisher;
    private final MessagePersistence messagePersistence;
    private final MessageEntityAdapter adapter;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 클라이언트로부터 채팅 메시지를 수신해 저장하고, 메시지 발행 후 결과를 반환합니다.
     *
     * @param request 클라이언트가 보낸 채팅 메시지 요청 DTO
     * @return 저장 및 발행된 메시지 정보를 담은 응답 DTO
     */
    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest request) {
        log.info("채팅 메시지 수신: {}", request);
        ChatMessage message = adapter.fromRequest(request);
        ChatMessage savedMessage = messagePersistence.saveMessage(message);
        log.info("채팅 메시지 저장 완료: {}", savedMessage);

        messagePublisher.publishMessage(savedMessage);
        log.info("채팅 메시지 발행 완료: {}", savedMessage);

        ChatMessageResponse response = adapter.toResponse(savedMessage);
        return ResponseEntity.ok(response);
    }
}
