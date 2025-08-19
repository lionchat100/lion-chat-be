package com.lion.be.chat.room.service;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.room.domain.entity.ChatRoom;
import com.lion.be.chat.room.domain.entity.ChatRoomUser;
import com.lion.be.chat.room.repository.ChatRoomRepository;
import com.lion.be.chat.room.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomPersistence {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public void updateChatRoomRecentMessage(ChatRoom chatRoom, ChatMessage message) {
        chatRoom.updateRecentMessage(message.getContent(), message.getCreatedAt());
        chatRoomRepository.save(chatRoom);
        log.debug("채팅방 마지막 내용, 시간 업데이트됨: {}번 방", chatRoom.getId());
    }

    public void updateChatRoomUserReadStatus(ChatRoomUser user, boolean isRead) {
        user.updateReadStatus(isRead);
        chatRoomUserRepository.save(user);
        log.debug("{}번 방 메시지 읽음 상태 업데이트됨", user.getChatRoom().getId());
    }
}
