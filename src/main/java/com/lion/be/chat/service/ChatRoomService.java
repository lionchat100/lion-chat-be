package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.entity.ChatRoom;

import java.util.List;

public interface ChatRoomService {

    ChatRoom createChatRoom(ChatMessageRequest chatMessageRequest);

    List<ChatRoom> getChatRooms(Long userId);
}
