package com.lion.be.chat.room.repository;

import com.lion.be.chat.room.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {
}
