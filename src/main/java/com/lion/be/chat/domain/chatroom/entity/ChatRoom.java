package com.lion.be.chat.domain.chatroom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {

    @Id
    private Long id;

    //멤버 멤버1id
    //멤버 멤버2id

    @Builder.Default
    private Boolean isDeleted = false;


    private String recentMessageContent;

    private LocalDateTime recentSentDate;



}
