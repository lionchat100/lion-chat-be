package com.lion.be.chat.domain.chatroomuser.repository;

import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long>,
        QuerydslPredicateExecutor<ChatRoomUser>,
        ChatRoomUserCustomRepository {
}
