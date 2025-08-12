package com.lion.be.userlike.domain.entity;

import com.lion.be.user.domain.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_user_id", nullable = false)
	private User fromUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_user_id", nullable = false)
	private User toUser;

	public UserLikes(User fromUser, User toUser) {
		this.fromUser = fromUser;
		this.toUser = toUser;
	}
}
