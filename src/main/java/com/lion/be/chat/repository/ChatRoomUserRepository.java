package com.lion.be.chat.repository;

import com.lion.be.chat.domain.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    Set<ChatRoomUser> findById_ChatRoomId(Long chatRoomId);
}
