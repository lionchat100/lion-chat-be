package com.lion.be.chat.repository;

import com.lion.be.chat.domain.dto.ChatRoomListResponse;

import java.util.List;

public interface ChatRoomUserCustomRepository {

    List<ChatRoomListResponse> findMyChatRoomListByUserId(Long userId);
}
