package com.lion.be.acceptance.feed;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.feed.FeedSteps.*;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사2_온보딩_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("피드 관련 기능 인수테스트")
public class FeedAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("피드 작성 성공")
    void createFeed() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);
        // when
        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> newResponse = 피드_하나를_조회한다(accessToken, spec, feedId);
        assertThat(newResponse.jsonPath().getLong("id")).isEqualTo(feedId);
        assertThat(newResponse.jsonPath().getString("title")).isEqualTo("Test Title");
        assertThat(newResponse.jsonPath().getString("content")).isEqualTo("Test Content");

    }

    @Test
    @DisplayName("피드 삭제 성공")
    void deleteFeed() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // when
        피드를_삭제한다(accessToken, spec, feedId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> deletedFeedResponse = 피드_하나를_조회한다(accessToken, spec,feedId);
        assertThat(deletedFeedResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("권한 없는 사용자 피드 삭제 실패")
    void deleteFeedWithoutPermisson() {
        // given
        var loginResponse1 = 비회원이_로그인한다(spec);
        String accessToken1 = loginResponse1.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken1, spec);

        var loginResponse2 = 원준이_로그인한다(spec);
        String accessToken2 = loginResponse2.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken2, spec);

        ExtractableResponse<Response> response = 피드를_작성한다(accessToken1, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // when
        ExtractableResponse<Response> deleteResponse = 피드를_삭제한다(accessToken2, spec, feedId);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ExtractableResponse<Response> deletedFeedResponse = 피드_하나를_조회한다(accessToken1, spec,feedId);
        assertThat(deletedFeedResponse.jsonPath().getLong("id")).isEqualTo(feedId);
        assertThat(deletedFeedResponse.jsonPath().getString("title")).isEqualTo("Test Title");
        assertThat(deletedFeedResponse.jsonPath().getString("content")).isEqualTo("Test Content");
    }

    @Test
    @DisplayName("피드 수정 성공")
    void updateFeed() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        ExtractableResponse<Response> feedReponse  = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = feedReponse.jsonPath().getLong("feedId");

        // when
        ExtractableResponse<Response> response = 피드를_수정한다(accessToken, spec, feedId, "Updated Title", "Updated Content");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> updatedFeedResponse = 피드_하나를_조회한다(accessToken, spec, feedId);
        assertThat(updatedFeedResponse.jsonPath().getLong("id")).isEqualTo(feedId);
        assertThat(updatedFeedResponse.jsonPath().getString("title")).isEqualTo("Updated Title");
        assertThat(updatedFeedResponse.jsonPath().getString("content")).isEqualTo("Updated Content");
    }

    @Test
    @DisplayName("권한 없는 사용자 피드 수정 실패")
    void updateFeedWithoutPermisson() {
        // given
        var loginResponse1 = 비회원이_로그인한다(spec);
        String accessToken1 = loginResponse1.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken1, spec);

        var loginResponse2 = 원준이_로그인한다(spec);
        String accessToken2 = loginResponse2.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken2, spec);

        ExtractableResponse<Response> feedReponse  = 피드를_작성한다(accessToken1, spec, "Test Title", "Test Content");
        Long feedId = feedReponse.jsonPath().getLong("feedId");


        // when
        ExtractableResponse<Response> response = 피드를_수정한다(accessToken2, spec, feedId, "Updated Title", "Updated Content");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ExtractableResponse<Response> unchangedResponse = 피드_하나를_조회한다(accessToken1, spec, feedId);
        assertThat(unchangedResponse.jsonPath().getLong("id")).isEqualTo(feedId);
        assertThat(unchangedResponse.jsonPath().getString("title")).isEqualTo("Test Title");
        assertThat(unchangedResponse.jsonPath().getString("content")).isEqualTo("Test Content");
    }


    @Test
    @DisplayName("최신 피드 조회")
    void getRecentFeeds() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);
        피드를_작성한다(accessToken, spec, "Test Title", "Test Content");

        // when
        ExtractableResponse<Response> response = 최신_피드를_조회한다(accessToken, spec);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("").size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("인기 피드 조회")
    void getHotFeeds() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), accessToken, spec);
        피드를_작성한다(accessToken, spec, "Test Title", "Test Content");

        // when
        ExtractableResponse<Response> response = 인기_피드를_조회한다(accessToken, spec);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("").size()).isGreaterThan(0);
    }
}
