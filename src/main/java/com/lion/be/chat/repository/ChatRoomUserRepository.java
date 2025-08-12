package com.lion.be.chat.repository;

import com.lion.be.chat.domain.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.id.chatRoomId = :chatRoomId")
    Set<ChatRoomUser> findById_ChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("""
        SELECT cru.chatRoom.id
        FROM ChatRoomUser cru
        WHERE cru.user.id IN (:userId1, :userId2)
        AND cru.chatRoom.isDeleted = false
        GROUP BY cru.chatRoom.id
        HAVING COUNT(DISTINCT cru.user.id) = 2
        """)
    Optional<Long> findChatRoomIdByTwoUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.id.chatRoomId = :chatRoomId AND cru.id.userId = :userId")
    ChatRoomUser findById_ChatRoomIdAndId_UserId(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    boolean existsById_ChatRoomIdAndId_UserId(Long chatRoomId, Long userId);
}
