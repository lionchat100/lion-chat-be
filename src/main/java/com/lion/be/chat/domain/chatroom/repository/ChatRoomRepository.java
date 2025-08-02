package com.lion.be.chat.domain.chatroom.repository;

import com.lion.be.chat.domain.chatroom.dto.ChatRoomListDto;
import com.lion.be.chat.domain.chatroom.entity.ChatRoom;
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
select 
new com.lion.be.chat.domain.chatroom.dto.ChatRoomListDto(cr.id, cr.regDt, cr.recentMessageContent, cr.recentMessageDt, u.name, u.id, cru_my.isRead) 
from ChatRoomUser cru_my
join ChatRoom cr on cru_my.chatRoom = cr
join ChatRoomUser cru_opponent on cru_my.chatRoom = cru_opponent.chatRoom
join User u on cru_opponent.user = u
where cru_my.user.id = :userId and cru_opponent.user.id != :userId and cr.isDeleted = false
order by cr.recentMessageDt desc
""")
    List<ChatRoomListDto> findAllChatRoom(Long userId);
}