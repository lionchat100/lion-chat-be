package com.lion.be.chat.domain.chatroomuser.repository;

import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import com.lion.be.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
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