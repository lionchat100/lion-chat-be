package com.lion.be.chat.domain.chatroomuser.repository;

import com.lion.be.chat.domain.chatroom.dto.ChatRoomListResponse;

import java.util.List;

public interface ChatRoomUserCustomRepository {

    List<ChatRoomListResponse> findMyChatRoomListByUserId(Long userId);
}
