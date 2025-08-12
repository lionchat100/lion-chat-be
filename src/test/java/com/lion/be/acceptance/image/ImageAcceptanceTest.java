package com.lion.be.acceptance.image;

import static com.lion.be.acceptance.image.ImageSteps.*;

import com.lion.be.acceptance.AcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("이미지 관련 인수 테스트")
@Disabled // 필요할 때만 주석처리하기
public class ImageAcceptanceTest extends AcceptanceTest {

    @DisplayName("이미지를 성공적으로 업로드한다.")
    @Test
    void uploadImage_success() throws IOException {
        // given
        api_문서_타이틀("image_upload_success", spec);

        // when
        ExtractableResponse<Response> response = 이미지를_업로드한다(회원_원준_액세스토큰, spec);

        // then
        이미지_업로드_성공을_검증한다(response);
    }

	@DisplayName("이미지 리스트를 성공적으로 업로드한다.")
	@Test
	void uploadImageList_success() throws IOException {
		// given
		api_문서_타이틀("image_list_upload_success", spec);

		// when
		ExtractableResponse<Response> response = 이미지_리스트를_업로드한다(회원_원준_액세스토큰, spec, 2);

		// then
		이미지_리스트_업로드_성공을_검증한다(response, 2);
	}

	@DisplayName("이미지 3장 리스트를 성공적으로 업로드한다.")
	@Test
	void uploadImageList_threeImages_success() throws IOException {
		// given
		api_문서_타이틀("image_list_upload_three_success", spec);

		// when
		ExtractableResponse<Response> response = 이미지_리스트를_업로드한다(회원_원준_액세스토큰, spec, 3);

		// then
		이미지_리스트_업로드_성공을_검증한다(response, 3);
	}


	@DisplayName("업로드된 이미지를 성공적으로 삭제한다.")
    @Test
    void deleteImage_success() throws IOException {
        // given
        api_문서_타이틀("image_delete_success", spec);
        // 먼저 이미지를 업로드하여 삭제할 대상의 ID를 얻는다.
        ExtractableResponse<Response> uploadResponse = 이미지를_업로드한다(회원_원준_액세스토큰, spec);
        Long imageId = uploadResponse.jsonPath().getLong("imageId");

        // when
        ExtractableResponse<Response> deleteResponse = 이미지를_삭제한다(imageId, 회원_원준_액세스토큰, spec);

        // then
        이미지_삭제_성공을_검증한다(deleteResponse);
    }

    @DisplayName("존재하지 않는 이미지를 삭제하려고 하면 404를 반환한다.")
    @Test
    void deleteImage_notFound() {
        // given
        api_문서_타이틀("image_delete_fail_not_found", spec);
        Long nonExistentImageId = 9999L;

        // when
        ExtractableResponse<Response> response = 이미지를_삭제한다(nonExistentImageId, 회원_원준_액세스토큰, spec);

        // then
        존재하지_않는_이미지_요청을_검증한다(response);
    }

	@DisplayName("다른 사용자의 이미지를 삭제하려고 하면 403을 반환한다.")
	@Test
	void deleteImage_accessDenied() throws IOException {
		// given
		api_문서_타이틀("image_delete_fail_access_denied", spec);
		// 회원_원준이 이미지를 업로드
		ExtractableResponse<Response> uploadResponse = 이미지를_업로드한다(회원_원준_액세스토큰, spec);
		Long imageId = uploadResponse.jsonPath().getLong("imageId");

		// when
		// 다른 회원(예: 회원_지혜)이 원준의 이미지를 삭제하려고 시도
		ExtractableResponse<Response> deleteResponse = 이미지를_삭제한다(imageId, 회원_토킷_액세스토큰, spec);

		// then
		다른_사용자의_이미지_삭제_실패를_검증한다(deleteResponse);
	}

}
