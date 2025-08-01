package com.lion.be.chat.domain.chatroom.repository;

import com.lion.be.chat.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
