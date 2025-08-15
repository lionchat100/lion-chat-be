package com.lion.be.chat.room.repository;

import com.lion.be.chat.room.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByChatRoomUsers_User_IdOrderByRecentMessageDtDesc(@Nullable Long id);
}
