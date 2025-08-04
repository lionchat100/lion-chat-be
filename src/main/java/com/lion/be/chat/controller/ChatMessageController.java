package com.lion.be.chat.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.dto.ChatMessageDto;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.dto.MessageAck;
import com.lion.be.chat.domain.entity.ChatMessage;
import com.lion.be.chat.service.ChatMessageReadService;
import com.lion.be.chat.service.ChatMessageWriteService;
import com.lion.be.chat.service.ChatRoomService;
import com.lion.be.chat.service.ChatRoomUserReadService;
import com.lion.be.chat.service.ChatRoomUserWriteService;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatmessages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageWriteService chatMessageWriteService;
    private final ChatMessageReadService chatMessageReadService;

    private final ChatRoomService chatRoomService;

    private final ChatRoomUserWriteService chatRoomUserWriteService;
    private final ChatRoomUserReadService chatRoomUserReadService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessage, Principal principal) {
        Long chatRoomId = chatMessage.getChatRoomId();
        if (!chatRoomService.isThereRoom(chatRoomId)) {
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        Long senderId = currentUser.getId();
        if (!chatRoomUserReadService.isThereRoomUser(chatRoomId, senderId)) {
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        String sender = currentUser.getName();
        String content = chatMessage.getContent();
        LocalDateTime date = chatMessage.getDate();

        // 1. 메시지를 저장하고 저장된 엔티티를 반환받음
        ChatMessage savedMessage = chatMessageWriteService.saveMessage(chatRoomId, sender, senderId, content, date);

        // 2. 후속 작업 수행
        chatRoomService.updateRecentMessage(chatRoomId, content, date);
        chatRoomUserWriteService.updateOpponentToUnread(chatRoomId, senderId);

        // 3. 저장된 엔티티의 정보로 DTO를 생성하여 브로드캐스팅
        messagingTemplate.convertAndSend("/topic/chatroom" + chatRoomId,
                new ChatMessageDto(
                        savedMessage.getId().toHexString(), // 실제 저장된 ID 사용
                        savedMessage.getSenderName(),
                        savedMessage.getSenderId(),
                        // Instant를 클라이언트가 사용하는 LocalDateTime으로 변환
                        LocalDateTime.ofInstant(savedMessage.getDate(), ZoneId.of("Asia/Seoul")),
                        savedMessage.getContent()
                ));
    }

    @MessageMapping("/chat.ack")
    public void acknowledgeMessage(@Payload MessageAck messageAck, Principal principal) {
        Long chatRoomId = messageAck.getChatRoomId();
        if (!chatRoomService.isThereRoom(chatRoomId)) {
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        Long senderId = messageAck.getSenderId();
        if (!chatRoomUserReadService.isThereRoomUser(chatRoomId, senderId)) {
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        Instant receiveAt = messageAck.getDate().toInstant(ZoneOffset.UTC);

        if (!currentUser.getId().equals(senderId)) {
            chatMessageWriteService.markAsRead(chatRoomId, currentUser.getId(), receiveAt);
            chatRoomUserWriteService.updateReceiverToRead(chatRoomId, currentUser.getId());
        }

    }

    @GetMapping("/{roomId}")
    public List<ChatMessageDto> loadAllMessages(@PathVariable("roomId") Long roomId,
                                                @RequestParam(value = "lastId", required = false) String lastId,
                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        if (!chatRoomService.isThereRoom(roomId)) {
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        //UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentMemberId = currentUser.getId();
        if (!chatRoomUserReadService.isThereRoomUser(roomId, currentMemberId)) {
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        chatMessageWriteService.markAsRead(roomId, currentUser.getId(), Instant.now());
        chatRoomUserWriteService.updateReceiverToRead(roomId, currentUser.getId());

        if (lastId != null && ObjectId.isValid(lastId)) {
            ObjectId lastMessageId = new ObjectId(lastId);

            return chatMessageReadService.afterRead(roomId, lastMessageId);
        }

        return chatMessageReadService.firstRead(roomId);
    }

}
