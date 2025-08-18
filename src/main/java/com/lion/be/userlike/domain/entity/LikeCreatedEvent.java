package com.lion.be.userlike.domain.entity;

public record LikeCreatedEvent(Long fromUserId, Long toUserId, String fromUserNickname) {
}
