package com.lion.be.chat.domain.chatroom.entity;

import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    private Long id;

    private Boolean isDeleted = false;

    private LocalDateTime regDt;

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    public ChatRoom(Boolean isDeleted, LocalDateTime regDt) {
        this.isDeleted = isDeleted;
        this.regDt = regDt;
    }

}
