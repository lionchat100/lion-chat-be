package com.lion.be.chat.message.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.message.domain.dto.ChatMessageRequest;
import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.message.service.MessagePersistence;
import com.lion.be.chat.message.service.MessageProcessor;
import com.lion.be.global.aop.CheckRateLimitChat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chatrooms/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final MessagePersistence messagePersistence;
    private final MessageProcessor messageProcessor;

    /**
     * 클라이언트로부터 채팅 메시지를 수신해 저장하고, 메시지 발행 후 결과를 반환합니다.
     *
     * @param request 클라이언트가 보낸 채팅 메시지 요청 DTO
     * @return 저장 및 발행된 메시지 정보를 담은 응답 DTO
     */
//    @PostMapping("/message")
//    public void sendMessageByRest(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestBody ChatMessageRequest request
//    ) {
//        messageProcessor.processIncomingMessage(request, userPrincipal.getId());
//    }
    @MessageMapping("/chat.sendMessage")
    @CheckRateLimitChat
    public void sendMessageByWebSocket(
            @Payload ChatMessageRequest request,
            Principal principal
    ) {
        if (principal == null) {
            throw new IllegalStateException("Cannot send message without a valid user principal.");
        }

        UserPrincipal userPrincipal = (UserPrincipal) ((Authentication) principal).getPrincipal();
        messageProcessor.processIncomingMessage(request, userPrincipal.getId());
    }

    /**
     * 채팅방의 이전 메시지 내역을 조회합니다.
     *
     * @param roomId 채팅방 ID
     * @param lastId 마지막으로 조회한 메시지 ID
     * @return 이전 메시지 목록
     */
    @PreAuthorize("@chatRoomUserRepository.existsById_ChatRoomIdAndId_UserId(#roomId, #userPrincipal.id)")
    @GetMapping(value = "/messages", params = {"roomId", "lastId"})
    public ResponseEntity<List<ChatMessageResponse>> getMessageHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long roomId,
            @RequestParam(defaultValue = "0") Long lastId
    ) {
        List<ChatMessageResponse> messages = messagePersistence.findMessagesByIdAndLastId(roomId, lastId);
        return ResponseEntity.ok(messages);
    }
}
