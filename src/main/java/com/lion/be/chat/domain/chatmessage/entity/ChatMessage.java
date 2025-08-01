package com.lion.be.chat.domain.chatmessage.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    private ObjectId id;

    private Long senderId;
    private Long chatRoomId;
    private String date;
    private String content;
    private Boolean isRead;

    public ChatMessage(Long senderId, Long chatRoomId, String date, String content, Boolean isRead) {
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.date = date;
        this.content = content;
        this.isRead = isRead;
    }

}
