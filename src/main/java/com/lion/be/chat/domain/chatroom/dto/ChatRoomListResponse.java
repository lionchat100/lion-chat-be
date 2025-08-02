package com.lion.be.chat.domain.chatroom.dto;

import java.time.LocalDateTime;

public record ChatRoomListResponse(
        Long roomId, // 채팅방 id
        String userImageUrl, // 프로필 이미지 URL
        String opponentName, // 상대방 이름
        String lastChat, // 최근 메시지
        LocalDateTime lastChatTime, // 마지막 연락 시각
        boolean isRead // 읽음 여부
) {}
