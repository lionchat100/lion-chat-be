package com.lion.be.chat.domain.entity;

import com.lion.be.chat.domain.entity.ChatRoom;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Setter
    private Boolean isRead;

    private LocalDateTime regDt;

    public ChatRoomUser(ChatRoom chatRoom, User user, Boolean isRead) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.isRead = isRead;
        this.regDt = LocalDateTime.now();
    }

}
