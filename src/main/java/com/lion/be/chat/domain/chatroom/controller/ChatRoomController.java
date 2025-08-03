package com.lion.be.chat.domain.chatroom.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.chatmessage.service.ChatMessageWriteService;
import com.lion.be.chat.domain.chatroom.dto.ChatRoomListDto;
import com.lion.be.chat.domain.chatroom.dto.OpponentUserRequest;
import com.lion.be.chat.domain.chatroom.entity.ChatRoom;
import com.lion.be.chat.domain.chatroom.service.ChatRoomReadService;
import com.lion.be.chat.domain.chatroom.service.ChatRoomWriteService;
import com.lion.be.chat.domain.chatroomuser.service.ChatRoomUserWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomWriteService chatRoomWriteService;
    private final ChatRoomReadService chatRoomReadService;
    @PostMapping
    public ChatRoom joinChatRoom(@RequestBody OpponentUserRequest opponentUser, @AuthenticationPrincipal UserPrincipal currentUser){
        Long opponentId = opponentUser.getId();
        //UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return chatRoomWriteService.joinChatRoom(currentUser.getId(), opponentId);
    }

    @GetMapping
    public List<ChatRoomListDto> getChatRoomList(@AuthenticationPrincipal UserPrincipal currentUser){
        //UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return chatRoomReadService.fetchAll(currentUser.getId());

    }

}
