package com.lion.be.chat.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Version
//    private Long version;

    private Boolean isDeleted = false;

    private LocalDateTime regDt;

    @OneToMany(mappedBy = "chatRoom", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    private String recentMessageContent;

    private ZonedDateTime recentMessageDt;

    private Boolean isRead;

    public ChatRoom(
            Long id,
//            Long version,
            Boolean isDeleted,
            LocalDateTime regDt,
            List<ChatRoomUser> chatRoomUsers,
            String recentMessageContent,
            ZonedDateTime recentMessageDt,
            Boolean isRead
    ) {
        this.id = id;
//        this.version = version;
        this.isDeleted = isDeleted;
        this.regDt = regDt;
        this.chatRoomUsers = chatRoomUsers;
        this.recentMessageContent = recentMessageContent;
        this.recentMessageDt = recentMessageDt;
        this.isRead = isRead;
    }

    public ChatRoom(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        this.regDt = LocalDateTime.now();
    }

    public void addChatRoomUser(ChatRoomUser chatRoomUser) {
        chatRoomUsers.add(chatRoomUser);
    }

}
