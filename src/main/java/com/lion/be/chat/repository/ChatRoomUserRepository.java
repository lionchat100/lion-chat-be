package com.lion.be.chat.repository;

import com.lion.be.chat.domain.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long>,
        QuerydslPredicateExecutor<ChatRoomUser>,
        ChatRoomUserCustomRepository {
    @Query("""
select cru from ChatRoomUser cru
where cru.chatRoom.id = :chatRoomId and cru.user.id != :userId
""")
    Optional<ChatRoomUser> findOpponent(Long chatRoomId, Long userId);

    @Query("""
select cru from ChatRoomUser cru
where cru.chatRoom.id = :chatRoomId and cru.user.id = :userId
""")
    Optional<ChatRoomUser> findMine(Long chatRoomId, Long userId);


}