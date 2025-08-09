package com.lion.be.acceptance.feed;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.feed.FeedSteps.상태코드_200이다;
import static com.lion.be.acceptance.feed.FeedSteps.상태코드_401이다;
import static com.lion.be.acceptance.feed.FeedSteps.상태코드_404이다;
import static com.lion.be.acceptance.feed.FeedSteps.인기_피드를_조회한다;
import static com.lion.be.acceptance.feed.FeedSteps.최신_피드를_조회한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드_삭제_후_조회_응답을_검증한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드_전체_조회_응답을_검증한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드_좋아요_정보를_검증한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드_하나를_조회한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드를_삭제한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드를_수정한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드를_작성한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드에_좋아요를_누른다;
import static com.lion.be.acceptance.feed.FeedSteps.피드의_동등성을_검증한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드의_좋아요를_취소한다;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사2_온보딩_요청;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("피드 관련 기능 인수테스트")
public class FeedAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("피드 작성 성공")
    void when_save_feed_then_response_200() {
        api_문서_타이틀("save_feed_success", spec);

        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);
        // when
        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // then
        상태코드_200이다(response);

    }

    @Test
    @DisplayName("피드 작성 시 검증")
    void check_equality_of_saved_feed() {
        api_문서_타이틀("save_feed_success", spec);

        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);
        // when
        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // then
        상태코드_200이다(response);

        ExtractableResponse<Response> newResponse = 피드_하나를_조회한다(accessToken, spec, feedId);
        피드의_동등성을_검증한다(newResponse, feedId, "Test Title", "Test Content");

    }

    @Test
    @DisplayName("피드 삭제 성공")
    void when_delete_feed_then_response_200() {
        api_문서_타이틀("delete_feed_success", spec);

        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // when
        피드를_삭제한다(accessToken, spec, feedId);

        // then
        상태코드_200이다(response);

    }

    @Test
    @DisplayName("피드 삭제를 검증")
    void when_find_deleted_feed_then_response_401() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // when
        피드를_삭제한다(accessToken, spec, feedId);
        상태코드_200이다(response);

        // then
        ExtractableResponse<Response> deletedFeedResponse = 피드_하나를_조회한다(accessToken, spec, feedId);
        상태코드_404이다(deletedFeedResponse);
    }

    @Test
    @DisplayName("권한 없는 사용자 피드 삭제 실패")
    void when_delete_feed_then_response_401() {
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
        상태코드_401이다(deleteResponse);

        ExtractableResponse<Response> deletedFeedResponse = 피드_하나를_조회한다(accessToken1, spec, feedId);
        피드의_동등성을_검증한다(deletedFeedResponse, feedId, "Test Title", "Test Content");
    }

    @Test
    @DisplayName("피드 수정 성공")
    void when_update_feed_then_response_200() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        ExtractableResponse<Response> feedReponse = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = feedReponse.jsonPath().getLong("feedId");

        // when
        ExtractableResponse<Response> response = 피드를_수정한다(accessToken, spec, feedId, "Updated Title",
                "Updated Content");

        // then
        상태코드_200이다(response);

        ExtractableResponse<Response> updatedFeedResponse = 피드_하나를_조회한다(accessToken, spec, feedId);
        피드의_동등성을_검증한다(updatedFeedResponse, feedId, "Updated Title", "Updated Content");
    }

    @Test
    @DisplayName("권한 없는 사용자 피드 수정 실패")
    void when_update_feed_then_response_401() {
        // given
        var loginResponse1 = 비회원이_로그인한다(spec);
        String accessToken1 = loginResponse1.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken1, spec);

        var loginResponse2 = 원준이_로그인한다(spec);
        String accessToken2 = loginResponse2.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken2, spec);

        ExtractableResponse<Response> feedReponse = 피드를_작성한다(accessToken1, spec, "Test Title", "Test Content");
        Long feedId = feedReponse.jsonPath().getLong("feedId");

        // when
        ExtractableResponse<Response> response = 피드를_수정한다(accessToken2, spec, feedId, "Updated Title",
                "Updated Content");

        // then
        상태코드_401이다(response);

        ExtractableResponse<Response> unchangedResponse = 피드_하나를_조회한다(accessToken1, spec, feedId);
        피드의_동등성을_검증한다(unchangedResponse, feedId, "Test Title", "Test Content");
    }

    @Test
    @DisplayName("최신 피드 조회")
    void when_fetch_all_recent_feed_then_response_200() {
        // given
        api_문서_타이틀("fetch_all_recent_feed_success", spec);
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        List<String> feedTitles = List.of("Test Title 1", "Test Title 2", "Test Title 3", "Test Title 4",
                "Test Title 5");
        List<String> feedContents = List.of("Test Content 1", "Test Content 2", "Test Content 3", "Test Content 4",
                "Test Content 5");

        for (int i = 0; i < 5; i++) {
            피드를_작성한다(accessToken, spec, feedTitles.get(i), feedContents.get(i));
        }

        // when
        ExtractableResponse<Response> response = 최신_피드를_조회한다(accessToken, spec);

        // then
        피드_전체_조회_응답을_검증한다(response, 5, feedTitles, feedContents);
    }

    @Test
    @DisplayName("피드 목록에서 삭제가 반영되는지 검증한다")
    void when_soft_delete_then_response_200() {
        // given
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        List<String> feedTitles = new ArrayList<>(
                List.of("Test Title 1", "Test Title 2", "Test Title 3", "Test Title 4", "Test Title 5"));
        List<String> feedContents = new ArrayList<>(
                List.of("Test Content 1", "Test Content 2", "Test Content 3", "Test Content 4", "Test Content 5"));

        for (int i = 0; i < 5; i++) {
            피드를_작성한다(accessToken, spec, feedTitles.get(i), feedContents.get(i));
        }

        // when
        피드를_삭제한다(accessToken, spec, 1L); // 첫 번째 피드를 삭제
        ExtractableResponse<Response> response = 최신_피드를_조회한다(accessToken, spec);
        feedTitles.remove(0);
        feedContents.remove(0); // 삭제된 피드의 제목과 내용을 목록에서 제거

        // then
        피드_삭제_후_조회_응답을_검증한다(response, 4, feedTitles, feedContents, "Test Title 1", "Test Content 1");
    }

    @Test
    @DisplayName("인기 피드 조회")
    void when_fetch_hot_recent_feed_then_response_200() {
        // given
        api_문서_타이틀("fetch_all_hot_feed_success", spec);
        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(UserFixture.회원_멋사2_온보딩_요청(), accessToken, spec);

        List<String> feedTitles = new ArrayList<>(
                List.of("Test Title 1", "Test Title 2", "Test Title 3", "Test Title 4", "Test Title 5"));
        List<String> feedContents = new ArrayList<>(
                List.of("Test Content 1", "Test Content 2", "Test Content 3", "Test Content 4", "Test Content 5"));

        for (int i = 0; i < 5; i++) {
            피드를_작성한다(accessToken, spec, feedTitles.get(i), feedContents.get(i));
        }

        // when
        ExtractableResponse<Response> response = 인기_피드를_조회한다(accessToken, spec);

        // then
        피드_전체_조회_응답을_검증한다(response, 5, feedTitles, feedContents);
    }

    @Test
    @DisplayName("피드에 좋아요를 누르면 likeCount가 1 증가하고 isLiked는 true가 된다.")
    void when_like_feed_then_likeCount_increases() {
        // given
        api_문서_타이틀("like_feed_success", spec);

        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        var feedResponse = 피드를_작성한다(accessToken, spec, "좋아요 테스트 피드", "내용");
        Long feedId = feedResponse.jsonPath().getLong("feedId");

        // when
        피드에_좋아요를_누른다(accessToken, spec, feedId);

        // then
        var fetchResponse = 피드_하나를_조회한다(accessToken, spec, feedId);
        피드_좋아요_정보를_검증한다(fetchResponse, feedId, 1, true);
    }

    @Test
    @DisplayName("좋아요를 누른 피드의 좋아요를 취소하면 likeCount가 0이 되고 isLiked는 false가 된다.")
    void when_unlike_feed_then_likeCount_decreases() {
        // given
        api_문서_타이틀("unlike_feed_success", spec);

        var loginResponse = 비회원이_로그인한다(spec);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken, spec);

        var feedResponse = 피드를_작성한다(accessToken, spec, "좋아요 취소 테스트 피드", "내용");
        Long feedId = feedResponse.jsonPath().getLong("feedId");
        피드에_좋아요를_누른다(accessToken, spec, feedId); // 먼저 좋아요를 누름

        // when
        피드의_좋아요를_취소한다(accessToken, spec, feedId);

        // then
        var fetchResponse = 피드_하나를_조회한다(accessToken, spec, feedId);
        피드_좋아요_정보를_검증한다(fetchResponse, feedId, 0, false);
    }

    @Test
    @DisplayName("여러 사용자가 피드에 좋아요를 누르면 likeCount가 증가한다.")
    void when_multiple_users_like_feed_then_likeCount_increases() {
        // given
        // 사용자 1 (비회원)
        var loginResponse1 = 비회원이_로그인한다(spec);
        String accessToken1 = loginResponse1.jsonPath().getString("accessToken");
        온보딩을_완료한다(회원_멋사2_온보딩_요청(), accessToken1, spec);

        // 사용자 2 (원준)
        String accessToken2 = 원준이_로그인한다(spec).jsonPath().getString("accessToken");
        온보딩을_완료한다(UserFixture.회원_멋사_온보딩_요청(), accessToken2, spec); // 원준 온보딩

        var feedResponse = 피드를_작성한다(accessToken1, spec, "멀티 좋아요 테스트 피드", "내용");
        Long feedId = feedResponse.jsonPath().getLong("feedId");

        // when
        피드에_좋아요를_누른다(accessToken1, spec, feedId);
        피드에_좋아요를_누른다(accessToken2, spec, feedId);

        // then
        // 사용자 1의 관점에서 조회
        var fetchResponse1 = 피드_하나를_조회한다(accessToken1, spec, feedId);
        피드_좋아요_정보를_검증한다(fetchResponse1, feedId, 2, true);

        // 사용자 2의 관점에서 조회
        var fetchResponse2 = 피드_하나를_조회한다(accessToken2, spec, feedId);
        피드_좋아요_정보를_검증한다(fetchResponse2, feedId, 2, true);
    }

}
