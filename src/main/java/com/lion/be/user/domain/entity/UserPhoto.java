package com.lion.be.user.domain.entity;

import com.lion.be.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user_photo")
public class UserPhoto extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String imageUrl;

	private int orderIndex; // 사진 순서 (1, 2, 3)

	public UserPhoto(User user, String imageUrl, int orderIndex) {
		this.user = user;
		this.imageUrl = imageUrl;
		this.orderIndex = orderIndex;
	}
}
