package com.lion.be.chat.message.repository;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

    @Aggregation(pipeline = {
            "{ $match: { status: 'PENDING' } }",
            "{ $match: { receiverId: ?0 } }"
    })
    List<ChatMessage> findPendingMessageByReceiverId(Long userId);

    /**
     * 채팅방의 메시지를 30개씩 조회합니다.
     * lastId가 0이면 최신 메시지부터, 0이 아니면 해당 ID 이후의 메시지부터 30개씩 조회합니다.
     *
     * @param roomId 채팅방 ID
     * @param lastId 마지막으로 조회한 메시지 ID (0이면 최신부터)
     * @param pageable 페이징 정보
     * @return 메시지 목록
     */
    @Query("{ 'chatRoomId': ?0 }")
    Page<ChatMessage> findMessagesByIdAndLastId(Long roomId, Pageable pageable);
}