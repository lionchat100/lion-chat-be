package com.lion.be.chat.domain.chatmessage.repository;

import com.lion.be.chat.domain.chatmessage.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

//    List<ChatMessage> getAllMessages(Long chatRoomId);
}
