package com.lion.be.userlike.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_likes",
	uniqueConstraints = @UniqueConstraint(columnNames = {"from_user_id", "to_user_id"})) // 중복 요청 했을경우를 위한 처리
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLikes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "from_user_id", nullable = false)
	private Long fromUserId;

	@Column(name = "to_user_id", nullable = false)
	private Long toUserId;

	public UserLikes(Long fromUserId, Long toUserId) {
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
	}
}
