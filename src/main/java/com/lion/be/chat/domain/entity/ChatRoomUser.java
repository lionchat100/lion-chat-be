package com.lion.be.chat.domain.entity;

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

import java.time.LocalDateTime;

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

    private Boolean isRead;

    private LocalDateTime regDt;

    public ChatRoomUser(
            ChatRoom chatRoom,
            User user,
            Boolean isRead
    ) {
        this.id = new ChatRoomUserId(chatRoom.getId(), user.getId());
        this.chatRoom = chatRoom;
        this.user = user;
        this.isRead = isRead;
        this.regDt = LocalDateTime.now();
    }

    public ChatRoomUser(Boolean isRead) {
        this.isRead = isRead;
        this.regDt = LocalDateTime.now();
    }

}
