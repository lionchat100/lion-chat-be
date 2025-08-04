package com.lion.be.chat.domain.chatroom.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.chatroom.dto.ChatRoomListResponse;
import com.lion.be.chat.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room")
    public ResponseEntity<List<ChatRoomListResponse>> getMyChatRooms(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(principal.getId()));
    }
}
