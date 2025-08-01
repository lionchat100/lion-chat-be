package com.lion.be.chat.domain.chatmessage.entity;


import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chat_message")
public class ChatMessage {
    //기본 생성되는 ObjectId를 활용한다. 이러면 sentDate를 활용하지 않아도 날짜별 정렬 가능 (기본적으로 ObjectId는 생성된 시간 정보를 포함하고 있음)
    @Id
    private String id;

    private Long senderId;

    private Long receiverId;

    private Long chatRoomId;

    //mongoDB와 잘 호환되는 날짜 자료형. DTO에서 LocalDateTime으로 한국 시간대에 맞게 변환할 것
    private Instant sentDate;

    private String content;
    private Boolean isRead;
}
