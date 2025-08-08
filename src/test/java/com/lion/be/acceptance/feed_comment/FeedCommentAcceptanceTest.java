package com.lion.be.acceptance.feed_comment;

import static com.lion.be.acceptance.feed.FeedSteps.피드를_작성한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.feedCommentSaveRequest_생성;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글_작성_응답을_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글_전체_조회_응답을_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_댓글을_작성한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_모든_댓글을_조회한다;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("피드 댓글 관련 인수 테스트")
class FeedCommentAcceptanceTest extends AcceptanceTest {

    String accessToken;

    @BeforeEach
    void before() {
        accessToken = 회원_원준_액세스토큰;
        var onboardingResponse = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .spec(spec)
                .auth().oauth2(accessToken)
                .log().all()
                .body(UserFixture.회원_멋사2_온보딩_요청()) // "멋사대학교"를 사용
                .when()
                .patch("/api/users/onboarding")
                .then()
                .log().all()
                .extract();
    }

    @Nested
    @DisplayName("피드 댓글 CRUD 테스트")
    class OnboardingTest {

        private Long feedId;

        @BeforeEach
        void before() {
            var response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
            feedId = response.jsonPath().getLong("feedId");
        }

        @DisplayName("피드의 댓글을 작성한다.")
        @Test
        void when_save_feed_comment_then_response_200() {
            // given
            api_문서_타이틀("save_feed_comment_success", spec);

            // when
            var response = 피드의_댓글을_작성한다(feedCommentSaveRequest_생성("이것은 댓글 내용입니다."), feedId, accessToken, spec);

            // then
            피드_댓글_작성_응답을_검증한다(response);
        }

        @DisplayName("피드의 댓글을 모두 조회한다.")
        @Test
        void when_fetch_all_feed_comment_then_response_200() {
            // given
            api_문서_타이틀("fetch_all_feed_comment_success", spec);

            // when
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글1"), feedId, accessToken, spec);
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글2"), feedId, accessToken, spec);
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글3"), feedId, accessToken, spec);
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글4"), feedId, accessToken, spec);

            var response = 피드의_모든_댓글을_조회한다(feedId, accessToken, spec);

            // then
            피드_댓글_전체_조회_응답을_검증한다(response);
        }

    }

}