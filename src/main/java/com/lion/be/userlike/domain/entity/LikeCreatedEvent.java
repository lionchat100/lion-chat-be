package com.lion.be.userlike.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeCreatedEvent {
	private final Long fromUserId;
	private final Long toUserId;
	private final String fromUserNickname;
}
