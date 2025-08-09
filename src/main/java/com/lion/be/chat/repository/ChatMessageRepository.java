package com.lion.be.chat.repository;

import com.lion.be.chat.domain.entity.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

    @Aggregation(pipeline = {
            "{ $match: { status: 'PENDING' } }",
            "{ $match: { receiverId: ?0 } }"
    })
    List<ChatMessage> findPendingMessageByReceiverId(Long userId);
}
