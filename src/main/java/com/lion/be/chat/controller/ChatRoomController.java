package com.lion.be.chat.controller;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.dto.ChatRoomInitRequest;
import com.lion.be.chat.domain.dto.ChatRoomInitResponse;
import com.lion.be.chat.domain.dto.ChatRoomResponse;
import com.lion.be.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param userPrincipal
     * @param request
     * @return 생성한 채팅방 id
     */
    @PostMapping("/init")
    public ResponseEntity<ChatRoomInitResponse> createChatRoom(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ChatRoomInitRequest request
    ) {
        Long chatRoomId = chatRoomService.findOrCreateChatRoom(userPrincipal.getId(), request.receiverId());
        log.info("채팅방 생성 완료: {}", chatRoomId);
        return ResponseEntity.ok(new ChatRoomInitResponse(
                chatRoomId
        ));
    }

    /**
     * 사용자의 채팅방을 조회합니다.
     * @param userPrincipal
     * @return 채팅방 목록 일괄조회
     */
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getMyChatRoomList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(chatRoomService.getChatRooms(userPrincipal.getId()));
    }
}
