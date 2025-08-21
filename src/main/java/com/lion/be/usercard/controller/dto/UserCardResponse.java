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
	List<Long> imageIds,
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
			user.getUserPhotos().stream()
				.map(UserPhoto::getId)
					.toList(),
			user.getBio(),
			user.getPreferenceType(),
			isLikedByMe
		);
	}

	/**
	 * N+1 문제 해결을 위한 새로운 메서드 (photos 직접 전달)
	 *
	 * @param user 사용자 엔티티
	 * @param photos 별도로 조회한 UserPhoto 목록
	 * @param isLikedByMe 좋아요 여부
	 */
	public static UserCardResponse from(User user, List<UserPhoto> photos, boolean isLikedByMe) {
		return new UserCardResponse(
			user.getId(),
			user.getNickname(),
			user.getUniversity(),
			user.getIsUniversityView(),
			user.getPosition(),
			user.getMbti(),
			photos.stream()
				.map(UserPhoto::getImageUrl)
				.toList(),
			user.getUserPhotos().stream()
				.map(UserPhoto::getId)
				.toList(),
			user.getBio(),
			user.getPreferenceType(),
			isLikedByMe
		);
	}
}
