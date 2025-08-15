package com.lion.be.chat.room.repository;

import com.lion.be.chat.domain.entity.QChatRoom;
import com.lion.be.chat.domain.entity.QChatRoomUser;
import com.lion.be.chat.room.domain.dto.ChatRoomResponse;
import com.lion.be.user.domain.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ChatRoomResponse> findChatRoomResponsesByUserIdQueryDsl(Long userId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatRoomUser currentUserRoom = new QChatRoomUser("currentUserRoom");
        QChatRoomUser otherUserRoom = new QChatRoomUser("otherUserRoom");
        QUser otherUser = QUser.user;

        return queryFactory
                .select(Projections.constructor(ChatRoomResponse.class,
                        chatRoom.id,
                        otherUser.name,
                        chatRoom.recentMessageContent,
                        chatRoom.recentMessageDt,
                        otherUser.imageUrl,
                        currentUserRoom.isRead.coalesce(true)
                ))
                .from(chatRoom)
                .join(chatRoom.chatRoomUsers, currentUserRoom)
                .join(chatRoom.chatRoomUsers, otherUserRoom)
                .join(otherUserRoom.user, otherUser)
                .where(
                        currentUserRoom.user.id.eq(userId)
                                .and(otherUserRoom.user.id.ne(userId))
                                .and(chatRoom.isDeleted.isFalse())
                )
                .orderBy(chatRoom.recentMessageDt.desc().nullsLast())
                .fetch();
    }
}
