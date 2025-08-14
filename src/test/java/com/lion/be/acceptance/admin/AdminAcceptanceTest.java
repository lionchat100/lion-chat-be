package com.lion.be.acceptance.admin;

import static com.lion.be.acceptance.admin.AdminSteps.*;
import static com.lion.be.acceptance.user.UserSteps.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lion.be.acceptance.AcceptanceTest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

class AdminAcceptanceTest extends AcceptanceTest {

	@Test
	@DisplayName("어드민이 사용자를 차단한다")
	void should_ban_user_when_admin_requests() {
		api_문서_타이틀("should_ban_user_when_admin_requests",spec);
		// given
		String 원준_사용자_EMAIL = 회원_email를_가져온다(spec, 회원_원준_액세스토큰).jsonPath().getString("email");
		String 차단_사유 = "부적절한 행동으로 인한 차단";

		// when
		ExtractableResponse<Response> response = 사용자를_차단한다(
			원준_사용자_EMAIL,
			차단_사유,
			어드민_멋사_액세스토큰, // 어드민 토큰
			spec
		);

		// then
		사용자_차단이_성공했는지_검증한다(response, 원준_사용자_EMAIL, 차단_사유);
	}

	@Test
	@DisplayName("어드민이 차단된 사용자를 해제한다")
	void should_unban_user_when_admin_requests() {
		api_문서_타이틀("should_unban_user_when_admin_requests",spec);
		// given
		String 토킷_사용자_EMAIL = 회원_email를_가져온다(spec, 회원_토킷_액세스토큰).jsonPath().getString("email");
		String 차단_사유 = "부적절한 행동으로 인한 차단";
		String 해제_사유 = "반성하여 차단 해제";

		// 먼저 사용자를 차단
		ExtractableResponse<Response> 차단_응답 = 사용자를_차단한다(
			토킷_사용자_EMAIL,
			차단_사유,
			어드민_멋사_액세스토큰, // 어드민 토큰
			spec
		);

		사용자_차단이_성공했는지_검증한다(차단_응답, 토킷_사용자_EMAIL, 차단_사유);

		// when
		ExtractableResponse<Response> 해지_응답 = 사용자_차단을_해제한다(
			토킷_사용자_EMAIL,
			해제_사유,
			어드민_멋사_액세스토큰,// 어드민 토큰사용
			spec
		);

		// then
		사용자_차단_해제가_성공했는지_검증한다(해지_응답, 토킷_사용자_EMAIL, 해제_사유);
	}

	@Test
	@DisplayName("일반 사용자가 밴 API에 접근하면 401이다")
	void should_deny_access_when_regular_user_tries_ban() {
		api_문서_타이틀("should_deny_access_when_regular_user_tries_ban",spec);
		// given
		String 토킷_사용자_EMAIL = 회원_email를_가져온다(spec, 회원_토킷_액세스토큰).jsonPath().getString("email");
		String 차단_사유 = "부적절한 행동으로 인한 차단";

		// when - 일반 사용자(원준)가 다른 사용자(토킷)를 차단하려고 시도
		ExtractableResponse<Response> response = 사용자를_차단한다(
			토킷_사용자_EMAIL,
			차단_사유,
			회원_원준_액세스토큰, // 일반 사용자 토큰 사용
			spec
		);

		// then
		권한이_없어서_접근이_거부되었는지_검증한다(response);
	}

	@Test
	@DisplayName("일반 사용자가 언밴 API에 접근하면 401이다")
	void should_deny_access_when_regular_user_tries_unban() {
		api_문서_타이틀("should_deny_access_when_regular_user_tries_unban",spec);
		// given
		String 토킷_사용자_EMAIL = 회원_email를_가져온다(spec, 회원_토킷_액세스토큰).jsonPath().getString("email");
		String 해제_사유 = "반성하여 차단 해제";

		// 먼저 어드민이 사용자를 차단
		사용자를_차단한다(토킷_사용자_EMAIL, "차단", 어드민_멋사_액세스토큰, spec);

		// when - 일반 사용자(원준)가 차단 해제를 시도
		ExtractableResponse<Response> response = 사용자_차단을_해제한다(
			토킷_사용자_EMAIL,
			해제_사유,
			회원_원준_액세스토큰, // 일반 사용자 토큰 사용
			spec
		);

		// then
		권한이_없어서_접근이_거부되었는지_검증한다(response);
	}
}
