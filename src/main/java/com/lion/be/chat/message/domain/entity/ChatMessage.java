package com.lion.be.chat.message.domain.entity;

import com.lion.be.chat.message.domain.dto.ChatMessageResponse;
import com.lion.be.chat.room.domain.MessageStatus;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

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
            Long senderId,
            String senderName,
            Long chatRoomId,
            ZonedDateTime createdAt,
            String content,
            boolean isRead,
            MessageStatus status
    ) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.chatRoomId = chatRoomId;
        this.createdAt = createdAt;
        this.content = content;
        this.isRead = isRead;
        this.status = status;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void updateMessageStatus(MessageStatus status) {
        this.status = status;
    }
}
