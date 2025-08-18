package com.lion.be.user.controller.dto;

public record UserProfileUpdateResponse(
	Long userId,
	String message

) {

	public static UserProfileUpdateResponse success(Long userId) {
		return new UserProfileUpdateResponse(
			userId,
			"수정이 완료되었습니다."
		);
	}
}
