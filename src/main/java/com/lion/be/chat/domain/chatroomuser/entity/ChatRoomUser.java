package com.lion.be.chat.domain.chatroomuser.entity;

import com.lion.be.chat.domain.chatroom.entity.ChatRoom;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoomUser {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Boolean isRead;

    private LocalDateTime regDt;

    public ChatRoomUser(Boolean isRead, LocalDateTime regDt) {
        this.isRead = isRead;
        this.regDt = LocalDateTime.now();
    }

}
