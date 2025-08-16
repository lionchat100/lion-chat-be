package com.lion.be.chat.room.domain.entity;

import com.lion.be.user.domain.entity.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "chat_room_user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"chat_room_id", "user_id"})
        }
)
public class ChatRoomUser {

    @EmbeddedId
    private ChatRoomUserId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatRoomId")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    private Boolean isRead = false;

    private ZonedDateTime regDt;

    private ChatRoomUser(ChatRoom chatRoom, User user) {
        this.id = new ChatRoomUserId(chatRoom.getId(), user.getId());
        this.chatRoom = chatRoom;
        this.user = user;
        this.isRead = true; // 새로 생성 시 읽음 상태로 설정
        this.regDt = ZonedDateTime.now();
    }

    public static ChatRoomUser create(ChatRoom chatRoom, User user) {
        return new ChatRoomUser(chatRoom, user);
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
