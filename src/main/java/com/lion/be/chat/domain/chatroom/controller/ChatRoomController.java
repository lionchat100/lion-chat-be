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

    /**
     * API 참고 사항:
     * 로그인 기능이 미완성되어 임시로 userId를 1L로 고정했습니다.
     * 로그인 기능이 완성되면 아래 주석 처리된 코드로 변경해야 합니다.
     * - `@PreAuthorize("isAuthenticated()")`로 인증된 사용자만 접근 허용
     * - `@AuthenticationPrincipal`을 통해 현재 로그인된 사용자의 ID를 가져옴
     */
    @GetMapping("/room")
    public List<ChatRoomListResponse> getMyChatRooms() {
        // TODO: 로그인 기능 구현 후, 아래의 주석 처리된 코드로 교체 필요
        return chatRoomService.getMyChatRooms(1L);
    }

//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/room")
//    public ResponseEntity<List<ChatRoomListResponse>> getMyChatRooms(@AuthenticationPrincipal UserPrincipal principal) {
//        return ResponseEntity.ok(chatRoomService.getMyChatRooms(principal.getId()));
//    }
}
