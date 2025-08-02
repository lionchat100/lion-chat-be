package com.lion.be.chat.domain.chatmessage.repository;

import com.lion.be.chat.domain.chatmessage.entity.ChatMessage;
import com.lion.be.chat.domain.chatroom.dto.LastMessageInfo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

//    List<ChatMessage> getAllMessages(Long chatRoomId);

    @Aggregation(pipeline = {
            "{ '$match': { 'chatRoomId': { '$in': ?0 } } }",
            "{ '$sort': { 'date': -1 } }",
            "{ '$group': { '_id': '$chatRoomId', 'roomId': { '$first': '$chatRoomId' }, 'lastChat': { '$first': '$content' }, 'lastChatTime': { '$first': '$date' } } }"
    })
    List<LastMessageInfo> findLastMessagesByChatRoomIds(List<Long> chatRoomIds);
}
