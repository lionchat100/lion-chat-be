package com.lion.be.chat.domain.chatmessage.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.ChatMessageValue;
import com.lion.be.chat.domain.chatmessage.dto.ChatMessageDto;
import com.lion.be.chat.domain.chatmessage.dto.ChatMessageRequest;
import com.lion.be.chat.domain.chatmessage.dto.MessageAck;
import com.lion.be.chat.domain.chatmessage.entity.ChatMessage;
import com.lion.be.chat.domain.chatmessage.service.ChatMessageReadService;
import com.lion.be.chat.domain.chatmessage.service.ChatMessageWriteService;
import com.lion.be.chat.domain.chatroom.service.ChatRoomReadService;
import com.lion.be.chat.domain.chatroom.service.ChatRoomWriteService;
import com.lion.be.chat.domain.chatroomuser.service.ChatRoomUserReadService;
import com.lion.be.chat.domain.chatroomuser.service.ChatRoomUserWriteService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatMessageController {
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatMessageWriteService chatMessageWriteService;
    private final ChatMessageReadService chatMessageReadService;

    private final ChatRoomWriteService chatRoomWriteService;
    private final ChatRoomReadService chatRoomReadService;

    private final ChatRoomUserWriteService chatRoomUserWriteService;
    private final ChatRoomUserReadService chatRoomUserReadService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessage, Principal principal){
        Long chatRoomId = chatMessage.getChatRoomId();
        if(!chatRoomReadService.isThereRoom(chatRoomId)){
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

        messagingTemplate.convertAndSend("/topic/chatroom"+chatRoomId,
            new ChatMessageDto(
                ObjectId.get().toHexString(),
                sender,
                senderId,
                date,
                content
            ));

        chatMessageWriteService.saveMessage(chatRoomId, sender,senderId, content, date);
        chatRoomWriteService.updateRecentMessage(chatRoomId, content, date);
        chatRoomUserWriteService.updateToUnread(chatRoomId, senderId);
    }

    @MessageMapping("/chat.ack")
    public void acknowledgeMessage(@Payload MessageAck messageAck, Principal principal) {
        Long chatRoomId = messageAck.getChatRoomId();
        if(!chatRoomReadService.isThereRoom(chatRoomId)){
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        Long senderId = messageAck.getSenderId();
        if(!chatRoomUserReadService.isThereRoomUser(chatRoomId, senderId)){
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        Instant receiveAt = messageAck.getDate().toInstant(ZoneOffset.UTC);

        if(!senderId.equals(currentUser.getId())) {
            chatMessageWriteService.markAsRead(chatRoomId, currentUser.getId(), receiveAt);
        }
    }

    @GetMapping("/{roomId}")
    public List<ChatMessageDto> loadAllMessages(@PathVariable("roomId") Long roomId, @RequestParam(value = "lastId",required = false) String lastId) {
        if(!chatRoomReadService.isThereRoom(roomId)){
            throw new IllegalArgumentException("Chat room does not exist.");
        }

        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentMemberId = currentUser.getId();
        if(!chatRoomUserReadService.isThereRoomUser(roomId, currentMemberId)){
            throw new IllegalArgumentException("You are not a member of this chat room.");
        }

        chatMessageWriteService.markAsRead(roomId, currentUser.getId(), Instant.now());
        chatRoomUserWriteService.updateToRead(roomId, currentUser.getId());

        if(lastId != null && ObjectId.isValid(lastId)) {
            ObjectId lastMessageId = new ObjectId(lastId);

            return chatMessageReadService.afterRead(roomId, lastMessageId);
        }

        return chatMessageReadService.firstRead(roomId);
    }
}
