package com.lion.be.acceptance.image;

import static com.lion.be.acceptance.image.ImageSteps.이미지_삭제_성공을_검증한다;
import static com.lion.be.acceptance.image.ImageSteps.이미지_업로드_성공을_검증한다;
import static com.lion.be.acceptance.image.ImageSteps.이미지를_삭제한다;
import static com.lion.be.acceptance.image.ImageSteps.이미지를_업로드한다;
import static com.lion.be.acceptance.image.ImageSteps.존재하지_않는_이미지_요청을_검증한다;

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

}
