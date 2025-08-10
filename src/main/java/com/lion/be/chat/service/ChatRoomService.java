package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.chat.domain.entity.ChatRoom;

public interface ChatRoomService {
    ChatRoom createChatRoom(ChatMessageRequest chatRoomRequest);
}
