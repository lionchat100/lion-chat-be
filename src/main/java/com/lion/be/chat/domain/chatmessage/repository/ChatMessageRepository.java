package com.lion.be.chat.domain.chatmessage.repository;

import com.lion.be.chat.domain.chatmessage.entity.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.time.Instant;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

    @Modifying


    @Query("{ 'chatRoomId': ?0, 'senderId': { '$ne': ?1 }, 'date': { '$lte': ?2 } }")
    @Update("{ '$set': { 'isRead': true } }")
    void updateReadStatus(Long chatRoomId, Long receiverId, Instant currentDt);

    // 요청 1과 동일
    List<ChatMessage> findByChatRoomId(Long chatRoomId, Pageable pageable);

    // 요청 2와 동일
    List<ChatMessage> findByChatRoomIdAndIdLessThan(Long chatRoomId, ObjectId cursorId, Pageable pageable);
}
