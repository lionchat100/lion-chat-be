package com.lion.be.acceptance.image;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ImageSteps {

    public static File createTestFile() throws IOException {
        File file = File.createTempFile("test-image", ".jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("dummy-image-content".getBytes());
        }
        file.deleteOnExit();
        return file;
    }

    public static ExtractableResponse<Response> 이미지를_업로드한다(
            String accessToken,
            RequestSpecification spec) throws IOException {

        File testFile = createTestFile();

        return RestAssured
                .given().spec(spec).log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("image", testFile, "image/jpeg") // "image"는 @RequestParam 이름과 일치해야 함
                .when()
                .post("/api/images/upload")
                .then().log().all()
                .extract();
    }

	/**
	 * 이미지 리스트 업로드 (온보딩용)
	 */
	public static ExtractableResponse<Response> 이미지_리스트를_업로드한다(
		String accessToken,
		RequestSpecification spec,
		int imageCount) throws IOException {

		RequestSpecification requestSpec = RestAssured
			.given().spec(spec).log().all()
			.auth().oauth2(accessToken)
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

		for (int i = 0; i < imageCount; i++) {
			requestSpec.multiPart("images", createTestFile(), "image/jpeg");
		}

		return requestSpec
			.when()
			.post("/api/images/upload/list")
			.then().log().all()
			.extract();
	}

	/**
	 * 기본 2장 이미지 리스트 업로드
	 */
	public static ExtractableResponse<Response> 이미지_리스트를_업로드한다(
		String accessToken,
		RequestSpecification spec) throws IOException {
		return 이미지_리스트를_업로드한다(accessToken, spec, 2);
	}


	public static ExtractableResponse<Response> 이미지를_삭제한다(
            Long imageId,
            String accessToken,
            RequestSpecification spec) {

        return RestAssured
                .given().spec(spec).log().all()
                .auth().oauth2(accessToken)
                .when()
                .delete("/api/images/{imageId}", imageId)
                .then().log().all()
                .extract();
    }

    public static void 이미지_업로드_성공을_검증한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("imageId")).isNotNull();
    }

	public static void 이미지_리스트_업로드_성공을_검증한다(ExtractableResponse<Response> response, int expectedCount) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("$")).hasSize(expectedCount);
		assertThat(response.jsonPath().getLong("[0].imageId")).isNotNull();
		assertThat(response.jsonPath().getString("[0].imageUrl")).isNotNull();
		if (expectedCount > 1) {
			assertThat(response.jsonPath().getLong("[1].imageId")).isNotNull();
			assertThat(response.jsonPath().getString("[1].imageUrl")).isNotNull();
		}
	}

    public static void 이미지_삭제_성공을_검증한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 존재하지_않는_이미지_요청을_검증한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

	public static void 다른_사용자의_이미지_삭제_실패를_검증한다(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

}
