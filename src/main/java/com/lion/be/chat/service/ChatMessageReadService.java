package com.lion.be.chat.service;

import com.lion.be.chat.domain.dto.ChatMessageDto;
import com.lion.be.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageReadService {

    private final ChatMessageRepository chatMessageRepository;
    private static final int PAGE_SIZE = 10;

    public List<ChatMessageDto> firstRead(Long chatRoomId){
        Pageable page = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "_id"));

        List<ChatMessageDto> result = chatMessageRepository.findByChatRoomId(chatRoomId,page)
                .stream()
                .map(msg -> new ChatMessageDto(
                        msg.getId().toHexString(),
                        msg.getSenderName(),
                        msg.getSenderId(),
                        LocalDateTime.ofInstant(msg.getDate(), ZoneId.of("Asia/Seoul")),
                        msg.getContent()
                ))
                .collect(Collectors.toList());;

        Collections.reverse(result);

        return result;
    }

    public List<ChatMessageDto> afterRead(Long chatRoomId, ObjectId lastId){
        Pageable page = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "_id"));

        List<ChatMessageDto> result =  chatMessageRepository.findByChatRoomIdAndIdLessThan(chatRoomId, lastId, page)
                .stream()
                .map(msg -> new ChatMessageDto(
                        msg.getId().toHexString(),
                        msg.getSenderName(),
                        msg.getSenderId(),
                        LocalDateTime.ofInstant(msg.getDate(), ZoneId.of("Asia/Seoul")),
                        msg.getContent()
                ))
                .collect(Collectors.toList());


        return result;
    }

    public List<ChatMessageDto> unreadMessages(Long roomId, Long currentMemberId) {
        return chatMessageRepository.fetchUnreadMessages(roomId,currentMemberId)
                .stream().map(msg -> new ChatMessageDto(
                        msg.getId().toHexString(),
                        msg.getSenderName(),
                        msg.getSenderId(),
                        LocalDateTime.ofInstant(msg.getDate(), ZoneId.of("Asia/Seoul")),
                        msg.getContent()
                )).collect(Collectors.toList());
    }
}
