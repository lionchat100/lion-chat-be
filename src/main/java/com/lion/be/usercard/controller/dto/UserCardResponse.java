package com.lion.be.usercard.controller.dto;

import java.util.List;

import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.University;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.UserPhoto;

public record UserCardResponse (
	Long userId,
	String name,
	University university,
	Boolean isUniversityVisible,
	Position position,
	List<String> imageUrls
) {
	/**
	 * User 엔티티에서 변환하는 정적 메서드
	 */
	public static UserCardResponse from(User user) {
		return new UserCardResponse(
			user.getId(),
			user.getName(),
			user.getUniversity(),
			user.getIsUniversityView(),
			user.getPosition(),
			user.getUserPhotos().stream()
				.map(UserPhoto::getImageUrl)
				.toList()
		);
	}
}
