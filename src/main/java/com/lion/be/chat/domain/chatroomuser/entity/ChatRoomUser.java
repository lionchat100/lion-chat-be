package com.lion.be.chat.domain.chatroomuser.entity;

import com.lion.be.chat.domain.chatroom.entity.ChatRoom;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    private User user;

    @Setter
    private Boolean isRead;

    private LocalDateTime regDt;

    public ChatRoomUser(Boolean isRead) {
        this.isRead = isRead;
        this.regDt = LocalDateTime.now();
    }

}
