package com.lion.be.chat.domain.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    private ObjectId id;

    private Long senderId;
    private String senderName;

    private Long chatRoomId;
    private Instant date;
    private String content;
    private Boolean isRead;

    public ChatMessage(Long senderId, String senderName, Long chatRoomId, Instant date, String content, Boolean isRead) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.chatRoomId = chatRoomId;
        this.date = date;
        this.content = content;
        this.isRead = isRead;
    }

}
