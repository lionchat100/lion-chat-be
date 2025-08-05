package com.lion.be.chat.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.dto.ChatRoomDto;
import com.lion.be.chat.domain.dto.ChatRoomListResponse;
import com.lion.be.chat.domain.dto.OpponentUserRequest;
import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ChatRoomListResponse>> getMyChatRooms(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(principal.getId()));
    }


    @PostMapping
    public ChatRoomDto joinChatRoom(@RequestBody OpponentUserRequest opponentUser, @AuthenticationPrincipal UserPrincipal currentUser){
        Long opponentId = opponentUser.getId();
        //UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return chatRoomService.joinChatRoom(currentUser.getId(), opponentId);
    }

}
