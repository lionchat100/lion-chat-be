package com.lion.be.acceptance.feed;

import static com.lion.be.acceptance.auth.AuthSteps.비회원이_로그인한다;
import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.feed.FeedSteps.*;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.feedCommentSaveRequest_생성;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_댓글을_작성한다;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사2_온보딩_요청;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.util.UserFixture;
import com.lion.be.feed.service.FeedLikeScheduler;
import com.lion.be.feed_comment.service.FeedCommentLikeScheduler;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("피드 관련 기능 인수테스트")
public class FeedAcceptanceTest extends AcceptanceTest {

    @Autowired
    private FeedLikeScheduler feedLikeScheduler;

    @Test
    @DisplayName("피드 작성 성공")
    void when_save_feed_then_response_200() throws IOException {
        api_문서_타이틀("save_feed_success", spec);

        // given
		String accessToken = 토킷_완전_온보딩();
        // when
        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // then
        상태코드_200이다(response);

    }

    @Test
    @DisplayName("피드 작성 시 검증")
    void check_equality_of_saved_feed() throws IOException {
        api_문서_타이틀("save_feed_success", spec);

        // given
		String accessToken = 토킷_완전_온보딩();
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
    void when_delete_feed_then_response_200() throws IOException {
        api_문서_타이틀("delete_feed_success", spec);

        // given
		String accessToken = 토킷_완전_온보딩();

        ExtractableResponse<Response> response = 피드를_작성한다(accessToken, spec, "Test Title", "Test Content");
        Long feedId = response.jsonPath().getLong("feedId");

        // when
        피드를_삭제한다(accessToken, spec, feedId);

        // then
        상태코드_200이다(response);

    }

    @Test
    @DisplayName("피드 삭제를 검증")
    void when_find_deleted_feed_then_response_404() throws IOException {
        // given
		String accessToken = 토킷_완전_온보딩();

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
    void when_delete_feed_then_response_401() throws IOException {
        // given
		String accessToken1 = 토킷_완전_온보딩();

		String accessToken2 = 원준_완전_온보딩();

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
    void when_update_feed_then_response_200() throws IOException {
        // given
		String accessToken = 토킷_완전_온보딩();

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
    void when_update_feed_then_response_401() throws IOException {
        // given
		String accessToken1 = 토킷_완전_온보딩();

		String accessToken2 = 원준_완전_온보딩();

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
    void when_fetch_all_recent_feed_then_response_200() throws IOException {
        // given
        api_문서_타이틀("fetch_all_recent_feed_success", spec);
		String accessToken = 토킷_완전_온보딩();

        List<String> feedTitles = List.of("Test Title 1", "Test Title 2", "Test Title 3", "Test Title 4",
                "Test Title 5");
        List<String> feedContents = List.of("Test Content 1", "Test Content 2", "Test Content 3", "Test Content 4",
                "Test Content 5");

        for (int i = 0; i < 5; i++) {
            피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(i), feedContents.get(i));
        }

        List<String> expectedTitles = List.of("Test Title 5", "Test Title 4", "Test Title 3", "Test Title 2",
                "Test Title 1");
        List<String> expectedContents = List.of("Test Content 5", "Test Content 4", "Test Content 3", "Test Content 2",
                "Test Content 1");
        List<Boolean> expectedLiked = List.of(false, false, false, false, false);

        // when
        ExtractableResponse<Response> response = 최신_피드를_조회한다(accessToken, spec);

        // then
        피드_전체_조회_응답을_검증한다(response, 5, expectedTitles, expectedContents, expectedLiked);
    }

    @Test
    @DisplayName("피드 목록에서 삭제가 반영되는지 검증한다")
    void when_soft_delete_then_response_200() throws IOException {
        // given
		String accessToken = 토킷_완전_온보딩();

        List<String> feedTitles = new ArrayList<>(
                List.of("Test Title 1", "Test Title 2", "Test Title 3", "Test Title 4", "Test Title 5"));
        List<String> feedContents = new ArrayList<>(
                List.of("Test Content 1", "Test Content 2", "Test Content 3", "Test Content 4", "Test Content 5"));

        for (int i = 0; i < 5; i++) {
            피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(i), feedContents.get(i));
        }

        List<String> expectedTitles = List.of("Test Title 5", "Test Title 4", "Test Title 3", "Test Title 2");
        List<String> expectedContents = List.of("Test Content 5", "Test Content 4", "Test Content 3", "Test Content 2");
        // when
        피드를_삭제한다(accessToken, spec, 1L); // 첫 번째 피드를 삭제
        ExtractableResponse<Response> response = 최신_피드를_조회한다(accessToken, spec);

        // then
        피드_삭제_후_조회_응답을_검증한다(response, 4, expectedTitles, expectedContents, "Test Title 1", "Test Content 1");
    }

    @Test
    @DisplayName("인기 피드 조회")
    void when_fetch_hot_recent_feed_then_response_200() throws IOException {
        // given
        api_문서_타이틀("fetch_all_hot_feed_success", spec);
		String accessToken1 = 토킷_완전_온보딩();
		String accessToken2 = 원준_완전_온보딩();

        List<String> feedTitles = new ArrayList<>(
                List.of("Test Title 1", "Test Title 2", "Test Title 3", "Test Title 4", "Test Title 5"));
        List<String> feedContents = new ArrayList<>(
                List.of("Test Content 1", "Test Content 2", "Test Content 3", "Test Content 4", "Test Content 5"));

        for (int i = 0; i < 5; i++) {
            피드의_제한을_조심하며_작성한다(accessToken1, spec, feedTitles.get(i), feedContents.get(i));
        }

        피드에_좋아요를_누른다(accessToken1, spec, 1L);
        피드에_좋아요를_누른다(accessToken2, spec, 1L); // id:1에 좋아요 2개 누름

        피드에_좋아요를_누른다(accessToken1, spec, 3L);
        피드에_좋아요를_누른다(accessToken2, spec, 3L); // id:3에 좋아요 2개 누름

        피드에_좋아요를_누른다(accessToken2, spec, 5L); // id:5에 좋아요 1개 누름

        //예상 출력 결과: 3, 1, 5, 4, 2

        List<String> expectedTitles = List.of("Test Title 3", "Test Title 1", "Test Title 5", "Test Title 4",
                "Test Title 2");
        List<String> expectedContents = List.of("Test Content 3", "Test Content 1", "Test Content 5", "Test Content 4",
                "Test Content 2");
        List<Boolean> expectedLiked1 = List.of(true, true, false, false, false);
        List<Boolean> expectedLiked2 = List.of(true, true, true, false, false);

        // when
        // 좋아요 수가 극적으로 변화하지 않는다면, Redis와 RDB의 좋아요 수 차이가 정렬에 유의미하게 영향을 주지 않을 것이다.
        // 하지만 테스트할 때는 RDB에 좋아요 수가 정확히 반영되게 하기 위해 억지로 Redis를 Flush한다.
        feedLikeScheduler.syncLikesToDb();

        ExtractableResponse<Response> response1 = 인기_피드를_조회한다(accessToken1, spec);
        ExtractableResponse<Response> response2 = 인기_피드를_조회한다(accessToken2, spec);

        // then
        피드_전체_조회_응답을_검증한다(response1, 5, expectedTitles, expectedContents, expectedLiked1);
        피드_전체_조회_응답을_검증한다(response2, 5, expectedTitles, expectedContents, expectedLiked2);
    }

    @Test
    @DisplayName("피드에 좋아요를 누르면 likeCount가 1 증가하고 isLiked는 true가 된다.")
    void when_like_feed_then_likeCount_increases() throws IOException {
        // given
        api_문서_타이틀("like_feed_success", spec);

		String accessToken = 토킷_완전_온보딩();

        var feedResponse = 피드를_작성한다(accessToken, spec, "좋아요 테스트 피드", "내용");
        Long feedId = feedResponse.jsonPath().getLong("feedId");

        // when
        피드에_좋아요를_누른다(accessToken, spec, feedId);

        // then
        var fetchResponse = 문서_없이_피드_하나를_조회한다(accessToken, feedId);
        피드_좋아요_정보를_검증한다(fetchResponse, feedId, 1, true);
    }

    @Test
    @DisplayName("좋아요를 누른 피드의 좋아요를 취소하면 likeCount가 0이 되고 isLiked는 false가 된다.")
    void when_unlike_feed_then_likeCount_decreases() throws IOException {
        // given
        api_문서_타이틀("unlike_feed_success", spec);

		String accessToken = 토킷_완전_온보딩();

        var feedResponse = 피드를_작성한다(accessToken, spec, "좋아요 취소 테스트 피드", "내용");
        Long feedId = feedResponse.jsonPath().getLong("feedId");
        피드에_좋아요를_누른다(accessToken, spec, feedId); // 먼저 좋아요를 누름

        // when
        피드의_좋아요를_취소한다(accessToken, spec, feedId);

        // then
        var fetchResponse = 문서_없이_피드_하나를_조회한다(accessToken, feedId);
        피드_좋아요_정보를_검증한다(fetchResponse, feedId, 0, false);
    }

    @Test
    @DisplayName("여러 사용자가 피드에 좋아요를 누르면 likeCount가 증가한다.")
    void when_multiple_users_like_feed_then_likeCount_increases() throws IOException {
        // given
        // 사용자 1 (토킷)
		String accessToken1 = 토킷_완전_온보딩();

        // 사용자 2 (원준)
		String accessToken2 = 원준_완전_온보딩(); // 원준 온보딩

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

    @Test
    @DisplayName("댓글을 작성했을 때 피드의 commentCount가 증가한다.")
    void when_write_comment_then_commentCount_increases() throws IOException {
        // given
        // 사용자 1 (토킷)
		String accessToken1 = 토킷_완전_온보딩();

        // 사용자 2 (원준)
		String accessToken2 = 원준_완전_온보딩(); // 원준 온보딩

        var feedResponse = 피드를_작성한다(accessToken1, spec, "멀티 좋아요 테스트 피드", "내용");
        Long feedId = feedResponse.jsonPath().getLong("feedId");

        // when
        피드의_댓글을_작성한다(feedCommentSaveRequest_생성("회원1작성"), feedId, accessToken1, spec);
        피드의_댓글을_작성한다(feedCommentSaveRequest_생성("회원2작성"),feedId, accessToken2, spec);

        // then
        var fetchResponse1 = 피드_하나를_조회한다(accessToken1, spec, feedId);
        피드_댓글_정보를_검증한다(fetchResponse1, feedId, 2);
    }

    @Test
    @DisplayName("내가 쓴 글의 목록을 최신 순으로 불러온다.")
    void when_call_my_feeds_then_return_my_feeds() throws IOException {
        // given
        api_문서_타이틀("fetch_my_feeds_success", spec);
		String accessToken1 = 토킷_완전_온보딩();
        String accessToken2 = 원준_완전_온보딩();

        List<String> feedTitles = List.of("내 피드 1", "내 피드 2", "내 피드 3");
        List<String> feedContents = List.of("내용 1", "내용 2", "내용 3");

        피드를_작성한다(accessToken2, spec, feedTitles.get(0), feedContents.get(0));
        for (int i = 1; i < feedTitles.size(); i++) {
            피드의_제한을_조심하며_작성한다(accessToken1, spec, feedTitles.get(i), feedContents.get(i));
        }

        List<String> expectedTitles = List.of("내 피드 3", "내 피드 2");
        List<String> expectedContents = List.of("내용 3", "내용 2");

        // when
        ExtractableResponse<Response> response = 내_피드를_조회한다(accessToken1, spec,null,null);

        // then
        피드_전체_조회_응답을_검증한다(response, 2, expectedTitles, expectedContents, List.of(false, false));
    }

    @Test
    @DisplayName("같은 유저가 3초 내에 글을 2번 쓰면 실패한다.")
    void when_write_feeds_twice_in_3seconds_then_429() throws IOException {
        //given
            api_문서_타이틀("feed_rate_limit_3_seconds_fail", spec);
		String accessToken = 토킷_완전_온보딩();

        List<String> feedTitles = List.of("내 피드 1", "내 피드 2");
        List<String> feedContents = List.of("내용 1", "내용 2");

        피드를_작성한다(accessToken, spec, feedTitles.get(0), feedContents.get(0));

        //when
        var response = 피드를_작성한다(accessToken, spec, feedTitles.get(1), feedContents.get(1));

        //then
        다회_요청제한을_검증한다(response);
    }


    @Test
    @DisplayName("같은 유저가 10초 내에 글을 5번 쓰면 실패한다.")
    void when_write_feeds_many_in_10seconds_then_429() throws IOException {
        //given
        api_문서_타이틀("feed_rate_limit_10_minutes_fail", spec);
		String accessToken = 토킷_완전_온보딩();

        List<String> feedTitles = List.of("내 피드 1", "내 피드 2","내 피드 3", "내 피드 4","내 피드 5", "내 피드 6");
        List<String> feedContents = List.of("내 피드 1", "내 피드 2","내 피드 3", "내 피드 4","내 피드 5", "내 피드 6");

        피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(0), feedContents.get(0));
        피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(1), feedContents.get(1));
        피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(2), feedContents.get(2));
        피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(3), feedContents.get(3));
        피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(4), feedContents.get(4));

        //when
        var response = 피드의_제한을_조심하며_작성한다(accessToken, spec, feedTitles.get(5), feedContents.get(5));

        //then
        다회_요청제한을_검증한다(response);
    }

}
