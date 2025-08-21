package com.lion.be.chat.message.service;

import com.lion.be.chat.message.domain.entity.ChatMessage;
import com.lion.be.chat.message.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageReadService {

    private final ChatMessageRepository chatMessageRepository;

    public Slice<ChatMessage> getMessages(Long roomId, String lastId) {
        Pageable pageable = PageRequest.of(0, 30, Sort.by("_id").descending());

        if (lastId == null || lastId.isEmpty()) {
            return chatMessageRepository.findByChatRoomId(roomId, pageable);
        } else {
            return chatMessageRepository.findMessagesByIdAndLastId(roomId, new ObjectId(lastId), pageable);
        }
    }

    public void updateMessagesAsRead(Slice<ChatMessage> messages, Long userId) {
        List<ObjectId> unreadMessageIds = messages.getContent().stream()
                .filter(message -> !message.getSenderId().equals(userId))
                .map(ChatMessage::getId)
                .collect(Collectors.toList());

        if (!unreadMessageIds.isEmpty()) {
            chatMessageRepository.markMessagesAsRead(unreadMessageIds);
        }
    }
}
