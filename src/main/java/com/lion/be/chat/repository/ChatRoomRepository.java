package com.lion.be.chat.repository;

import com.lion.be.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
select cru.chatRoom from ChatRoomUser cru
where cru.user.id in (:user1Id,:user2Id) and cru.chatRoom.isDeleted = false
group by cru.chatRoom.id
having count(cru.user.id) = 2
""")
    Optional<ChatRoom> findChatRoomIdInTwo(Long user1Id, Long user2Id);

    @Query("""
select cr from ChatRoom cr
where cr.id = :chatRoomId and cr.isDeleted = false
""")
    Optional<ChatRoom> findChatRoom(Long chatRoomId);
}