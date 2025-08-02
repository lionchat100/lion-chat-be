package com.lion.be.chat.domain.chatroom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isDeleted = false;

    private LocalDateTime regDt;

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    @Setter
    private String recentMessageContent;

    @Setter
    private LocalDateTime recentMessageDt;

    public ChatRoom(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        this.regDt = LocalDateTime.now();
    }

    public void addChatRoomUser(ChatRoomUser chatRoomUser) {
        chatRoomUsers.add(chatRoomUser);
    }

}
