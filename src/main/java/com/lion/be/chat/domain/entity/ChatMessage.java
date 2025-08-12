package com.lion.be.chat.domain.entity;

import com.lion.be.chat.domain.MessageStatus;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.ZonedDateTime;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor
public class ChatMessage {

    @Id
    private ObjectId id;

    private Long senderId;

    private String senderName;

    private Long chatRoomId;

    private ZonedDateTime createdAt;

    private String content;

    private Boolean isRead;

    private MessageStatus status;

    public ChatMessage(
            ObjectId id,
            Long senderId,
            String senderName,
            Long chatRoomId,
            ZonedDateTime createdAt,
            String content,
            Boolean isRead,
            MessageStatus status
    ) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
        this.content = content;
        this.isRead = isRead;
        this.status = status;
    }

    public ChatMessage(
            Long senderId,
            Long chatRoomId,
            ZonedDateTime createdAt,
            String content,
            Boolean isRead,
            MessageStatus status
    ) {
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
        this.content = content;
        this.isRead = isRead;
        this.status = status;
    }

    public void markAsRead() {
        this.isRead = true;
        updateStatus(MessageStatus.READ);
    }

    public void updateStatus(MessageStatus status) {
        this.status = status;
    }
}
