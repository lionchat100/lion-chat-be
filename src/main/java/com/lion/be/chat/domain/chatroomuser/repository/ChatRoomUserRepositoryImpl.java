package com.lion.be.chat.domain.chatroomuser.repository;

import com.lion.be.chat.domain.chatmessage.repository.ChatMessageRepository;
import com.lion.be.chat.domain.chatroom.dto.ChatRoomListResponse;
import com.lion.be.chat.domain.chatroom.dto.LastMessageInfo;
import com.lion.be.chat.domain.chatroomuser.entity.ChatRoomUser;
import com.lion.be.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRoomUserRepositoryImpl implements ChatRoomUserCustomRepository {

    private final EntityManager em;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public List<ChatRoomListResponse> findMyChatRoomListByUserId(Long userId) {
        // ChatRoomUser, ChatRoom, User 조회
        List<ChatRoomUser> myChatRoomUsers = em.createQuery(
                        "SELECT cru FROM ChatRoomUser cru " +
                                "JOIN FETCH cru.chatRoom cr " +
                                "JOIN FETCH cru.user u " +
                                "JOIN FETCH cr.chatRoomUsers opponentCru " +
                                "JOIN FETCH opponentCru.user opponent " +
                                "WHERE u.id = :userId", ChatRoomUser.class)
                .setParameter("userId", userId)
                .getResultList();

        // 채팅방 ID 목록 MongoDB에 적용
        List<Long> myChatRoomIds = myChatRoomUsers.stream()
                .map(cru -> cru.getChatRoom().getId())
                .collect(Collectors.toList());

        // 모든 채팅방의 최근 메시지 조회
        List<LastMessageInfo> lastMessages = chatMessageRepository.findLastMessagesByChatRoomIds(myChatRoomIds);

        Map<Long, LastMessageInfo> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(LastMessageInfo::roomId, info -> info));

        // DTO로 변환
        return myChatRoomUsers.stream()
                .map(myCru -> {
                    Long chatRoomId = myCru.getChatRoom().getId();

                    // 상대방 User를 찾는 로직 개선: 스트림 대신 List 순회를 활용
                    User opponentUser = null;
                    for (ChatRoomUser chatRoomUser : myCru.getChatRoom().getChatRoomUsers()) {
                        if (!chatRoomUser.getUser().getId().equals(userId)) {
                            opponentUser = chatRoomUser.getUser();
                            break;
                        }
                    }

                    LastMessageInfo lastMessageInfo = lastMessageMap.getOrDefault(chatRoomId, null);

                    return new ChatRoomListResponse(
                            chatRoomId,
                            opponentUser != null ? opponentUser.getImageUrl() : null,
                            opponentUser != null ? opponentUser.getName() : null,
                            lastMessageInfo != null ? lastMessageInfo.lastChat() : null,
                            lastMessageInfo != null ? lastMessageInfo.lastChatTime() : null,
                            myCru.getIsRead()
                    );
                })
                .collect(Collectors.toList());
    }
}
