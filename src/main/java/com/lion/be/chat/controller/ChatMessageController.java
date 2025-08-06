package com.lion.be.chat.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.dto.ChatMessageDto;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.MessageAck;
import com.lion.be.chat.service.ChatMessageReadService;
import com.lion.be.chat.service.ChatMessageWriteService;
import com.lion.be.chat.service.ChatRoomService;
import com.lion.be.chat.service.ChatRoomUserReadService;
import com.lion.be.chat.service.ChatRoomUserWriteService;
import java.security.Principal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chats/message")
@RequiredArgsConstructor
public class ChatMessageController {

    private final JmsTemplate jmsTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatRoomUserReadService chatRoomUserReadService;
    private final ChatMessageReadService chatMessageReadService; // REST API용으로 유지
    private final ChatMessageWriteService chatMessageWriteService; // REST API용으로 유지
    private final ChatRoomUserWriteService chatRoomUserWriteService; // REST API용으로 유지

    /**
     * STOMP 메시지 수신 및 ActiveMQ 발행
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessageRequest,
                            Principal principal) {
        Long chatRoomId = chatMessageRequest.getChatRoomId();

        // ✨ Principal에서 UserPrincipal을 꺼내오는 로직 추가
        UserPrincipal currentUser = (UserPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        // 1. 유효성 검사 (빠르게 실패시키기 위해 Controller 단에서 수행)
        if (!chatRoomService.isThereRoom(chatRoomId)) {
            log.error("Chat room {} does not exist.", chatRoomId);
            // 에러 처리: 특정 사용자에게 에러 메시지를 보내거나 로깅
            return;
        }
        if (!chatRoomUserReadService.isThereRoomUser(chatRoomId, currentUser.getId())) {
            log.error("User {} is not a member of chat room {}.", currentUser.getId(), chatRoomId);
            return;
        }

        // 2. DTO에 발신자 정보 추가
        chatMessageRequest.setSenderId(currentUser.getId());

        // 3. ActiveMQ로 메시지 발행
        String destination = "chat.queue"; // ActiveMQ queue name


        // ✨ JmsTemplate이 Topic을 사용하도록 명시적으로 설정
        jmsTemplate.setPubSubDomain(false);

        log.info("Publishing message to ActiveMQ. Destination: {}, Payload: {}", destination,
                chatMessageRequest.getContent());
        jmsTemplate.convertAndSend(destination, chatMessageRequest);
    }

    /**
     * 메시지 읽음 처리 (ack)
     * 이 로직도 비동기 처리가 가능하지만, 실시간성이 매우 중요하고 부하가 적다면 동기로 유지해도 무방합니다.
     * 비동기로 전환하려면 sendMessage와 동일한 패턴을 따릅니다. (RabbitMQ 발행 -> Listener 처리)
     * 여기서는 일단 기존 로직을 유지하겠습니다.
     */
    @MessageMapping("/chat.ack")
    public void acknowledgeMessage(@Payload MessageAck messageAck, Principal principal) {
        UserPrincipal currentUser = (UserPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        Long chatRoomId = messageAck.getChatRoomId();
        if (!chatRoomService.isThereRoom(chatRoomId)) {
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        Long senderId = messageAck.getSenderId(); // 이 senderId는 메시지를 보낸 사람
        if (!chatRoomUserReadService.isThereRoomUser(chatRoomId, currentUser.getId())) {
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        // 메시지를 읽는 사람은 currentUser 이므로, 본인이 보낸 메시지에 대한 ack가 아니어야 함
        if (!currentUser.getId().equals(senderId)) {
            chatMessageWriteService.markAsRead(chatRoomId, currentUser.getId(),
                    messageAck.getDate().toInstant(ZoneOffset.UTC));
            chatRoomUserWriteService.updateReceiverToRead(chatRoomId, currentUser.getId());
        }
    }

    /**
     * 이전 메시지 로딩 (REST API)
     * 이 부분은 수정할 필요 없습니다. RESTful GET 요청이므로 그대로 유지합니다.
     */
    @GetMapping("/{roomId}")
    public List<ChatMessageDto> loadAllMessages(@PathVariable("roomId") Long roomId,
                                                @RequestParam(value = "lastId", required = false) String lastId,
                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        if (!chatRoomService.isThereRoom(roomId)) {
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        Long currentMemberId = currentUser.getId();
        if (!chatRoomUserReadService.isThereRoomUser(roomId, currentMemberId)) {
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        // 방에 들어왔을 때 이전 메시지들을 읽음 처리
        chatMessageWriteService.markAsRead(roomId, currentMemberId, Instant.now());
        chatRoomUserWriteService.updateReceiverToRead(roomId, currentMemberId);

        if (lastId != null && ObjectId.isValid(lastId)) {
            return chatMessageReadService.afterRead(roomId, new ObjectId(lastId));
        }
        return chatMessageReadService.firstRead(roomId);
    }

    //재연결 -> 재구독까지 웹소켓으로 날아온 메시지들을 읽기 위한 코드
    @GetMapping("/{roomId}/unread")
    public List<ChatMessageDto> loadUnreadMessages(@PathVariable("roomId") Long roomId,
                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        if (!chatRoomService.isThereRoom(roomId)) {
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        //UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentMemberId = currentUser.getId();
        if (!chatRoomUserReadService.isThereRoomUser(roomId, currentMemberId)) {
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        List<ChatMessageDto> result =  chatMessageReadService.unreadMessages(roomId, currentMemberId);
        chatMessageWriteService.markAsRead(roomId, currentMemberId, Instant.now());
        return result;
    }

}

