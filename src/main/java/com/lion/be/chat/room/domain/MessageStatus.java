package com.lion.be.chat.room.domain;

public enum MessageStatus {
    PENDING, // 서버가 클라이언트로부터 요청을 수신하고 DB 저장까지 완료한 상태
    PUBLISHED, // 서버에서 RabbitMQ로 발행이 완료된 상태
    DELIVERED, // RabbitMQ에서 클라이언트로 메시지를 전달한 상태
    REJECTED // 차단 등의 이유로 메시지가 수신되지 않는 상태
}