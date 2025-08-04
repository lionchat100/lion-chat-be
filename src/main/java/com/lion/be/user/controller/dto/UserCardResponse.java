package com.lion.be.user.controller.dto;

import java.util.List;

import com.lion.be.user.domain.Gender;
import com.lion.be.user.domain.Mbti;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.domain.entity.UserPhoto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCardResponse {
	private Long userId;
	private String name;
	private String university;
	private String position;
	private Mbti mbti;
	private Gender gender;
	private List<String> imageUrls; // 사진 1~3장

	// User 엔티티에서 변환하는 정적 메서드
	public static UserCardResponse from(User user) {
		return UserCardResponse.builder()
			.userId(user.getId())
			.name(user.getName())
			.university(user.getUniversity())
			.position(user.getPosition())
			.mbti(user.getMbti())
			.gender(user.getGender())
			.imageUrls(user.getUserPhotos().stream()
				.map(UserPhoto::getImageUrl)
				.toList())
			.build();
	}
}
