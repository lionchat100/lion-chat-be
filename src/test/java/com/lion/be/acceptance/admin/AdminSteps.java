package com.lion.be.acceptance.admin;

import static com.lion.be.acceptance.user.UserSteps.상태코드를_검증한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AdminSteps {

	public static ExtractableResponse<Response> 사용자를_차단한다(
		String email,
		String reason,
		String adminAccessToken,
		RequestSpecification spec) {

		Map<String, Object> banRequest = Map.of(
			"email", email,
			"reason", reason
		);

		return RestAssured
			.given()
			.spec(spec)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.auth().oauth2(adminAccessToken)
			.body(banRequest)
			.log().all()
			.when()
			.post("/api/admin/ban")
			.then()
			.log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 사용자_차단을_해제한다(
		String email,
		String reason,
		String adminAccessToken,
		RequestSpecification spec) {

		Map<String, Object> unbanRequest = Map.of(
			"email", email,
			"reason", reason
		);

		return RestAssured
			.given()
			.spec(spec)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.auth().oauth2(adminAccessToken)
			.body(unbanRequest)
			.log().all()
			.when()
			.patch("/api/admin/unban")
			.then()
			.log().all()
			.extract();
	}

	public static void 사용자_차단이_성공했는지_검증한다(ExtractableResponse<Response> response, String email, String reason) {
		Assertions.assertAll(
			() -> 상태코드를_검증한다(response, HttpStatus.OK),
			() -> assertThat(response.jsonPath().getString("bannedUserEmail")).isEqualTo(email),
			() -> assertThat(response.jsonPath().getString("role")).isEqualTo("BANNED"),
			() -> assertThat(response.jsonPath().getString("reason")).isEqualTo(reason),
			() -> assertThat(response.jsonPath().getString("message")).isEqualTo("사용자 정지를 성공하였습니다."),
			() -> assertThat(response.jsonPath().getString("bannedAt")).isNotNull()
		);
	}

	public static void 사용자_차단_해제가_성공했는지_검증한다(ExtractableResponse<Response> response, String email, String reason) {
		Assertions.assertAll(
			() -> 상태코드를_검증한다(response, HttpStatus.OK),
			() -> assertThat(response.jsonPath().getString("unbanUserEmail")).isEqualTo(email),
			() -> assertThat(response.jsonPath().getString("role")).isEqualTo("USER"),
			() -> assertThat(response.jsonPath().getString("reason")).isEqualTo(reason),
			() -> assertThat(response.jsonPath().getString("message")).isEqualTo("사용자 해지를 성공하였습니다."),
			() -> assertThat(response.jsonPath().getString("unbanAt")).isNotNull()
		);
	}

	public static void 권한이_없어서_접근이_거부되었는지_검증한다(ExtractableResponse<Response> response) {
		상태코드를_검증한다(response, HttpStatus.UNAUTHORIZED);
	}
}
