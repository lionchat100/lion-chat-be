package com.lion.be.chat.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.ChatMessageResponse;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.repository.ChatRoomRepository;
import com.lion.be.chat.repository.MessageEntityAdapter;
import com.lion.be.chat.service.MessagePersistence;
import com.lion.be.chat.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatrooms/chats")
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
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ChatMessageRequest request
    ) {
        log.info("채팅 메시지 수신: {}", request);

        if (!chatRoomRepository.existsById(request.chatRoomId())) {
            log.info("채팅방 비동기 생성 시작: {}", request.chatRoomId());
        }

        ChatMessage message = adapter.fromRequest(request);
        ChatMessage savedMessage = messagePersistence.saveMessage(message);
        log.info("채팅 메시지 저장 완료: {}", savedMessage);

        messagePublisher.publishMessage(savedMessage);
        log.info("채팅 메시지 발행 완료: {}", savedMessage);

        ChatMessageResponse response = adapter.toResponse(savedMessage, false); //todo isEnd를 어떻게 둘까?
        return ResponseEntity.ok(response);
    }

    /**
     * 채팅방의 초기 메시지 목록을 조회합니다.
     *
     * @param roomId 채팅방 ID
     * @return 최근 메시지 목록
     */
    @GetMapping("/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getInitialMessages(
            @RequestParam Long roomId
    ) {
        Page<ChatMessageResponse> messages = messagePersistence.findMessagesByIdAndLastId(
                roomId, 0L, PageRequest.of(0, 30));
        return ResponseEntity.ok(messages);
    }

    /**
     * 채팅방의 이전 메시지 내역을 조회합니다.
     *
     * @param roomId 채팅방 ID
     * @param lastId 마지막으로 조회한 메시지 ID
     * @return 이전 메시지 목록
     */
    @GetMapping("/messages/history")
    public ResponseEntity<Page<ChatMessageResponse>> getMessageHistory(
            @RequestParam Long roomId,
            @RequestParam Long lastId
    ) {
        Page<ChatMessageResponse> messages = messagePersistence.findMessagesByIdAndLastId(
                roomId, lastId, PageRequest.of(0, 30));
        return ResponseEntity.ok(messages);
    }
}
