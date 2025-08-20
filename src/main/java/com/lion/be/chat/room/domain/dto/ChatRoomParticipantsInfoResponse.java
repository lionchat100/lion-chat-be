package com.lion.be.chat.room.domain.dto;

public record ChatRoomParticipantsInfoResponse(
        Long senderId,
        Long receiverId,
        String senderNickname,
        String receiverNickname
) {
    public static ChatRoomParticipantsInfoResponse toResponse(
            Long senderId,
            Long receiverId,
            String senderNickname,
            String receiverNickname
    ) {
        return new ChatRoomParticipantsInfoResponse(
                senderId,
                receiverId,
                senderNickname,
                receiverNickname
        );
    }
}
