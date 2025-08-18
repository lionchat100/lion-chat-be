package com.lion.be.usercard.controller.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.Position;
import com.lion.be.user.domain.PreferenceType;
import com.lion.be.user.domain.University;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.UserPhoto;

public record UserCardResponse (
	Long userId,
	String nickname,
	University university,
	Boolean isUniversityVisible,
	Position position,
	Mbti mbti,
	List<String> imageUrls,
	String bio,
	@JsonProperty("focusType")
	PreferenceType preferenceType,
	boolean isLikedByMe // 로그인된 유저기준으로 카드를 좋아했는지 안했는지 체크하는 값
) {
	/**
	 * User 엔티티에서 변환하는 정적 메서드
	 */
	public static UserCardResponse from(User user, boolean isLikedByMe) {
		return new UserCardResponse(
			user.getId(),
			user.getNickname(),
			user.getUniversity(),
			user.getIsUniversityView(),
			user.getPosition(),
			user.getMbti(),
			user.getUserPhotos().stream()
				.map(UserPhoto::getImageUrl)
				.toList(),
			user.getBio(),
			user.getPreferenceType(),
			isLikedByMe
		);
	}
}
