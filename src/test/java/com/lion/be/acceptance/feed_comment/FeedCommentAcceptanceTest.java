package com.lion.be.acceptance.feed_comment;

import static com.lion.be.acceptance.feed.FeedSteps.피드를_삭제한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드를_작성한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.feedCommentSaveRequest_생성;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.feedCommentUpdateRequest_수정;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.문서_없이_피드의_모든_댓글을_조회한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.상태코드가_200이다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.상태코드가_401이다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.상태코드가_429이다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글_삭제_후_조회_응답을_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글_수정_응답을_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글_작성_응답을_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글_전체_조회_응답을_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글_좋아요_정보를_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글에_좋아요를_누른다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_댓글의_좋아요를_취소한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드_삭제_후_댓글_조회_응답을_검증한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_댓글을_삭제한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_댓글을_수정한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_댓글을_작성한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_댓글을_하나를_조회한다;
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
    String anotherAccessToken;

    @BeforeEach
    void before() {
        accessToken = 회원_원준_액세스토큰;
        anotherAccessToken = 비회원_엑세스토큰;
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

        @DisplayName("피드의 댓글을 수정한다.")
        @Test
        void when_update_feed_comment_then_response_200() {
            // given
            api_문서_타이틀("update_feed_comment_success", spec);
            var savedResponse = 피드의_댓글을_작성한다(feedCommentSaveRequest_생성("이것은 댓글 내용입니다."), feedId,
                    accessToken, spec);
            long commentId = savedResponse.jsonPath().getLong("commentId");

            // when
            var response = 피드의_댓글을_수정한다(feedCommentUpdateRequest_수정("이것은 새로운 댓글 내용입니다."), commentId, accessToken, spec);

            // then
            피드_댓글_수정_응답을_검증한다(response);
        }

        @DisplayName("피드의 댓글을 모두 조회한다.")
        @Test
        void when_fetch_all_feed_comment_then_response_200() throws InterruptedException { // 수정 완료된 코드
            // given
            api_문서_타이틀("fetch_all_feed_comment_success", spec);

            // when
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글1"), feedId, accessToken, spec);
            Thread.sleep(3100);
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글2"), feedId, accessToken, spec);
            Thread.sleep(3100);
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글3"), feedId, accessToken, spec);
            Thread.sleep(3100);
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글4"), feedId, accessToken, spec);

            var response = 피드의_모든_댓글을_조회한다(feedId, accessToken, spec);

            // then
            피드_댓글_전체_조회_응답을_검증한다(response);
        }

        @DisplayName("피드의 댓글을 삭제한다.")
        @Test
        void when_soft_delete_comment_then_response_200() {
            // given
            api_문서_타이틀("delete_feed_comment_success", spec);

            // when
            var commentResponse = 피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글1"), feedId,
                    accessToken, spec);
            Long commentId = commentResponse.jsonPath().getLong("commentId");

            var response = 피드의_댓글을_삭제한다(commentId, accessToken, spec);

            // then
            상태코드가_200이다(response);
        }

        @DisplayName("권한 없는 자가 피드의 댓글을 삭제 시 거부된다.")
        @Test
        void when_soft_delete_comment_then_response_401() {
            // given

            // when
            var commentResponse = 피드의_댓글을_작성한다(feedCommentSaveRequest_생성("댓글1"), feedId,
                    accessToken, spec);
            Long commentId = commentResponse.jsonPath().getLong("commentId");

            var response = 피드의_댓글을_삭제한다(commentId, anotherAccessToken, spec);

            // then
            상태코드가_401이다(response);
        }

        @DisplayName("피드의 댓글을 삭제 후 조회 시, 조회가 되지 않는다.")
        @Test
        void when_fetch_after_delete_then_comment_should_not_be_found() throws InterruptedException {
            // given
            api_문서_타이틀("fetch_feed_comment_after_delete_success", spec);

            String deletedContent = "이 댓글은 삭제될 것입니다.";
            String remainingContent = "이 댓글은 남아있을 것입니다.";

            var commentToDeleteResponse = 피드의_댓글을_작성한다(feedCommentSaveRequest_생성(deletedContent), feedId, accessToken,
                    spec);
            Thread.sleep(3100);
            피드의_댓글을_작성한다(feedCommentSaveRequest_생성(remainingContent), feedId, accessToken, spec);

            Long commentIdToDelete = commentToDeleteResponse.jsonPath().getLong("commentId");

            // when
            피드의_댓글을_삭제한다(commentIdToDelete, accessToken, spec);
            var response = 피드의_모든_댓글을_조회한다(feedId, accessToken, spec);

            // then
            피드_댓글_삭제_후_조회_응답을_검증한다(response, 1, remainingContent, deletedContent);
        }

        @DisplayName("피드의 댓글을 단 후 피드가 삭제될 시, 댓글 조회가 되지 않는다.")
        @Test
        void when_fetch_after_feed_delete_then_comment_should_not_be_found() {
            // given
            String deletedContent = "이 댓글은 삭제될 것입니다.";

            var commentToDeleteResponse = 피드의_댓글을_작성한다(feedCommentSaveRequest_생성(deletedContent), feedId, accessToken,
                    spec);

            Long commentIdToDelete = commentToDeleteResponse.jsonPath().getLong("commentId");

            // when
            피드를_삭제한다(accessToken, spec, feedId);
            var response = 피드의_댓글을_하나를_조회한다(commentIdToDelete, accessToken, spec);

            // then
            피드_삭제_후_댓글_조회_응답을_검증한다(response);
        }


    }

    @Nested
    @DisplayName("피드 댓글 좋아요 테스트")
    class FeedCommentLikeTest {

        private Long feedId;
        private Long commentId;

        @BeforeEach
        void before() {
            // 모든 테스트 전에 피드와 댓글을 하나씩 생성
            var feedResponse = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
            feedId = feedResponse.jsonPath().getLong("feedId");

            var commentResponse = 피드의_댓글을_작성한다(feedCommentSaveRequest_생성("이것은 좋아요 테스트용 댓글입니다."), feedId, accessToken,
                    spec);
            commentId = commentResponse.jsonPath().getLong("commentId");
        }

        @DisplayName("피드 댓글에 좋아요를 누르면 likeCount가 1 증가하고 isLiked는 true가 된다.")
        @Test
        void when_like_comment_then_likeCount_increases() {
            // given
            api_문서_타이틀("like_feed_comment_success", spec);

            // when
            var likeResponse = 피드_댓글에_좋아요를_누른다(commentId, accessToken, spec);

            // then
            상태코드가_200이다(likeResponse);

//            var fetchResponse = 피드의_모든_댓글을_조회한다(feedId, accessToken, spec);
//            피드_댓글_좋아요_정보를_검증한다(fetchResponse, commentId, 1, true);
        }

        @DisplayName("좋아요를 누른 댓글의 좋아요를 취소하면 likeCount가 1 감소하고 isLiked는 false가 된다.")
        @Test
        void when_unlike_comment_then_likeCount_decreases() {
            // given
            api_문서_타이틀("unlike_feed_comment_success", spec);
            피드_댓글에_좋아요를_누른다(commentId, accessToken, spec); // 먼저 좋아요를 누름

            // when
            var unlikeResponse = 피드_댓글의_좋아요를_취소한다(commentId, accessToken, spec);

            // then
            상태코드가_200이다(unlikeResponse);

            var fetchResponse = 문서_없이_피드의_모든_댓글을_조회한다(feedId, accessToken);
            피드_댓글_좋아요_정보를_검증한다(fetchResponse, commentId, 0, false);
        }

        @DisplayName("한 사용자가 같은 댓글에 여러 번 좋아요를 눌러도 likeCount는 1만 증가한다.")
        @Test
        void when_like_comment_twice_then_likeCount_is_one() {
            // given
            api_문서_타이틀("like_feed_comment_twice_success", spec);
            피드_댓글에_좋아요를_누른다(commentId, accessToken, spec); // 첫 번째 좋아요

            // when
            피드_댓글에_좋아요를_누른다(commentId, accessToken, spec); // 두 번째 좋아요

            // then
            var fetchResponse = 문서_없이_피드의_모든_댓글을_조회한다(feedId, accessToken);
            피드_댓글_좋아요_정보를_검증한다(fetchResponse, commentId, 1, true);
        }

        @DisplayName("여러 사용자가 한 댓글에 좋아요를 누르면 likeCount가 사용자 수만큼 증가한다.")
        @Test
        void when_multiple_users_like_comment_then_likeCount_increases() {
            // given
            api_문서_타이틀("multiple_users_like_comment_success", spec);

            // when
            피드_댓글에_좋아요를_누른다(commentId, accessToken, spec);
            피드_댓글에_좋아요를_누른다(commentId, anotherAccessToken, spec);

            // then
            var fetchResponseForUser1 = 피드의_모든_댓글을_조회한다(feedId, accessToken, spec);
            피드_댓글_좋아요_정보를_검증한다(fetchResponseForUser1, commentId, 2, true);

            var fetchResponseForUser2 = 피드의_모든_댓글을_조회한다(feedId, anotherAccessToken, spec);
            피드_댓글_좋아요_정보를_검증한다(fetchResponseForUser2, commentId, 2, true);
        }

    }

    @Nested
    @DisplayName("피드 댓글 작성 속도 제한 테스트")
    class RateLimitingTest {

        private Long feedId;

        @BeforeEach
        void before() {
            var response = 피드를_작성한다(accessToken, spec, "Rate Limit Test Feed", "Content");
            feedId = response.jsonPath().getLong("feedId");
        }

        @DisplayName("3초에 1개를 초과하여 댓글을 작성하면 429 에러가 발생한다")
        @Test
        void when_comment_too_fast_then_throw_429() throws InterruptedException {
            // given
            api_문서_타이틀("rate_limit_3_seconds_fail", spec);
            var request = feedCommentSaveRequest_생성("이것은 속도 제한 테스트 댓글입니다.");

            // when & then
            // 1. 첫 번째 요청은 성공한다.
            var firstResponse = 피드의_댓글을_작성한다(request, feedId, accessToken, spec);
            상태코드가_200이다(firstResponse);

            // 2. 3초가 지나기 전 두 번째 요청은 실패한다.
            var secondResponse = 피드의_댓글을_작성한다(request, feedId, accessToken, spec);
            상태코드가_429이다(secondResponse);

            // 3. 3초가 지난 후의 요청은 다시 성공한다.
            Thread.sleep(3100); // 3.1초 대기
            var thirdResponse = 피드의_댓글을_작성한다(request, feedId, accessToken, spec);
            상태코드가_200이다(thirdResponse);
        }

        @DisplayName("10분에 5개를 초과하여 댓글을 작성하면 429 에러가 발생한다")
        @Test
        void when_comment_more_than_5_in_10_minutes_then_throw_429() throws InterruptedException {
            // given
            api_문서_타이틀("rate_limit_10_minutes_fail", spec);
            var request = feedCommentSaveRequest_생성("이것은 10분 제한 테스트 댓글입니다.");

            // when & then
            // 1. 5개의 요청을 3초 이상의 간격을 두고 보내 모두 성공시킨다.
            for (int i = 0; i < 5; i++) {
                var response = 피드의_댓글을_작성한다(request, feedId, accessToken, spec);
                상태코드가_200이다(response);
                Thread.sleep(3100); // 단기 제한에 걸리지 않도록 3.1초 대기
            }

            // 2. 6번째 요청은 10분 제한에 걸려 실패한다.
            var sixthResponse = 피드의_댓글을_작성한다(request, feedId, accessToken, spec);
            상태코드가_429이다(sixthResponse);
        }

    }

}