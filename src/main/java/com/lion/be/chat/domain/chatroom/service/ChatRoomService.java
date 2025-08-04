package com.lion.be.chat.domain.chatroom.service;

import com.lion.be.chat.domain.chatroom.dto.ChatRoomListResponse;
import com.lion.be.chat.domain.chatroomuser.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomUserRepository chatRoomUserRepository;

    /**
     * 사용자의 채팅방 목록을 조회합니다.
     *
     * @param userId 조회하려는 사용자의 ID
     * @return 채팅방 목록
     */
    public List<ChatRoomListResponse> getMyChatRooms(Long userId) {
        return chatRoomUserRepository.findMyChatRoomListByUserId(userId);
    }
}
