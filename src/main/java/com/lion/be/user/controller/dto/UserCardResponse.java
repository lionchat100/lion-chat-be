package com.lion.be.user.controller.dto;

import java.util.List;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.entity.University;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.UserPhoto;

public record UserCardResponse (
	Long userId,
	String name,
	University university,
	Position position,
	Mbti mbti,
	Gender gender,
	List<String> imageUrls
)
	// User 엔티티에서 변환하는 정적 메서드
	{public static UserCardResponse from(User user) {
		return new UserCardResponse(
			user.getId(),
			user.getName(),
			user.getUniversity(),
			user.getPosition(),
			user.getMbti(),
			user.getGender(),
			user.getUserPhotos().stream()
				.map(UserPhoto::getImageUrl)
				.toList()
		);
	}
}
