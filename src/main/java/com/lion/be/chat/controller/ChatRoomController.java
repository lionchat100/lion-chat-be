package com.lion.be.chat.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoom>> getMyChatRoomList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(chatRoomService.getChatRooms(userPrincipal.getId()));
    }
}
