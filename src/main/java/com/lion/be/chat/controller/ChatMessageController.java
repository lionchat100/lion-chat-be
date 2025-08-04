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
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.*;
import java.util.List;

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
    public void sendMessage(@Payload ChatMessageRequest chatMessage, Principal principal){


        Long chatRoomId = chatMessage.getChatRoomId();
        if(!chatRoomService.isThereRoom(chatRoomId)){
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        Long senderId = currentUser.getId();
        if(!chatRoomUserReadService.isThereRoomUser(chatRoomId, senderId)){
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        String sender = currentUser.getUsername();
        String content = chatMessage.getContent();

        LocalDateTime date = chatMessage.getDate();

        chatMessageWriteService.saveMessage(chatRoomId, sender,senderId, content, date);
        chatRoomService.updateRecentMessage(chatRoomId, content, date);
        chatRoomUserWriteService.updateOpponentToUnread(chatRoomId, senderId);

        messagingTemplate.convertAndSend("/topic/chatroom"+chatRoomId,
            new ChatMessageDto(
                ObjectId.get().toHexString(),
                sender,
                senderId,
                date,
                content
            ));

    }

    @MessageMapping("/chat.ack")
    public void acknowledgeMessage(@Payload MessageAck messageAck, Principal principal) {
        Long chatRoomId = messageAck.getChatRoomId();
        if(!chatRoomService.isThereRoom(chatRoomId)){
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        Long senderId = messageAck.getSenderId();
        if(!chatRoomUserReadService.isThereRoomUser(chatRoomId, senderId)){
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        Instant receiveAt = messageAck.getDate().toInstant(ZoneOffset.UTC);

        if(!currentUser.getId().equals(senderId)) {
            chatMessageWriteService.markAsRead(chatRoomId, currentUser.getId(), receiveAt);
            chatRoomUserWriteService.updateReceiverToRead(chatRoomId, currentUser.getId());
        }

    }

    @GetMapping("/{roomId}")
    public List<ChatMessageDto> loadAllMessages(@PathVariable("roomId") Long roomId,
                                                @RequestParam(value = "lastId",required = false) String lastId,
                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        if(!chatRoomService.isThereRoom(roomId)){
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        //UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentMemberId = currentUser.getId();
        if(!chatRoomUserReadService.isThereRoomUser(roomId, currentMemberId)){
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        chatMessageWriteService.markAsRead(roomId, currentUser.getId(), Instant.now());
        chatRoomUserWriteService.updateReceiverToRead(roomId, currentUser.getId());

        if(lastId != null && ObjectId.isValid(lastId)) {
            ObjectId lastMessageId = new ObjectId(lastId);

            return chatMessageReadService.afterRead(roomId, lastMessageId);
        }

        return chatMessageReadService.firstRead(roomId);
    }
}
