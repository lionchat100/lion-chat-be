package com.lion.be.admin.controller.dto;

import jakarta.validation.constraints.NotNull;

public record BanUserRequest (
	@NotNull(message = "사용자 email는 필수입니다")
	String email,
	String reason // 차단 사유 (선택사항)
	){
}
