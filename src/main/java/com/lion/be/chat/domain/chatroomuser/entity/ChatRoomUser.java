package com.lion.be.chat.domain.chatroomuser.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUser {
    @Id
    private Long id;

    private Long chatRoomId;

    private Long userId;

    private Boolean isRead;
}
