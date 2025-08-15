package com.lion.be.chat.room.repository;

import com.lion.be.chat.room.domain.dto.ChatRoomResponse;
import com.lion.be.chat.room.domain.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {

    ChatRoom save(ChatRoom chatRoom);

    void delete(ChatRoom chatRoom);

    Optional<ChatRoom> findById(Long id);

    List<ChatRoomResponse> findChatRoomListByUserId(Long userId);
}
