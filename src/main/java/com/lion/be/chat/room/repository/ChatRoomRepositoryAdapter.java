package com.lion.be.chat.room.repository;

import com.lion.be.chat.room.domain.dto.ChatRoomResponse;
import com.lion.be.chat.room.domain.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatRoomRepositoryAdapter implements ChatRoomRepository {

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final ChatRoomQueryDslRepository chatRoomQueryDslRepository;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomJpaRepository.save(chatRoom);
    }

    @Override
    public void delete(ChatRoom chatRoom) {
        chatRoomJpaRepository.delete(chatRoom);
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        return chatRoomJpaRepository.findById(id);
    }

    @Override
    public List<ChatRoomResponse> findChatRoomListByUserId(Long userId) {
        return chatRoomQueryDslRepository.findChatRoomResponsesByUserIdQueryDsl(userId);
    }
}
