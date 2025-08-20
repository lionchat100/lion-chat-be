package com.lion.be.acceptance.notification;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.acceptance.user.UserSteps;
import com.lion.be.acceptance.util.UserFixture;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lion.be.acceptance.AcceptanceTest.api_문서_타이틀;
import static com.lion.be.acceptance.auth.AuthSteps.원준이_로그인한다;
import static com.lion.be.acceptance.auth.AuthSteps.토킷이_로그인한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드를_작성한다;
import static com.lion.be.acceptance.feed_comment.FeedCommentSteps.피드의_댓글을_작성한다;
import static com.lion.be.acceptance.feed.FeedSteps.피드에_좋아요를_누른다;
import static com.lion.be.acceptance.image.ImageSteps.이미지_리스트를_업로드한다;
import static com.lion.be.acceptance.notification.NotificationSteps.알림_전체_조회_응답을_검증한다;
import static com.lion.be.acceptance.notification.NotificationSteps.알림을_조회한다;
import static com.lion.be.acceptance.user.UserSteps.온보딩을_완료한다;
import static com.lion.be.acceptance.userlikes.UserLikesSteps.좋아요를_누른다;
import static com.lion.be.acceptance.chat.ChatSteps._1대1_채팅방을_생성_또는_조회한다;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사2_온보딩_요청;
import static com.lion.be.acceptance.util.UserFixture.회원_멋사_온보딩_요청;

@DisplayName("알림 기능 인수테스트")
public class NotificationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("다른 사람이 댓글 작성 시 알림 조회 가능")
    void comment_alarm_success() throws IOException {
        //given

        String accessToken1=토킷_완전_온보딩();
        String accessToken2=원준_완전_온보딩();

        ExtractableResponse<Response> resp1 = 피드를_작성한다(accessToken1, spec, "테스트", "테스트");
        Long feedId = resp1.jsonPath().getLong("feedId");
        Map<String, Object> saveReq = new HashMap<>();
        saveReq.put("content", "테스트");
        피드의_댓글을_작성한다(saveReq, feedId, accessToken2, spec);

        //when

        ExtractableResponse<Response> response =  알림을_조회한다(accessToken1, spec);

        //then
        알림_전체_조회_응답을_검증한다(response, 1);

    }

    @Test
    @DisplayName("다른 사람이 피드 좋아요 시 알림 조회 가능")
    void feed_like_alarm_success() throws IOException {
        //given

        String accessToken1=토킷_완전_온보딩();
        String accessToken2=원준_완전_온보딩();

        ExtractableResponse<Response> resp1 = 피드를_작성한다(accessToken1, spec, "테스트", "테스트");
        Long feedId = resp1.jsonPath().getLong("feedId");
        피드에_좋아요를_누른다(accessToken2, spec, feedId);

        //when

        ExtractableResponse<Response> response =  알림을_조회한다(accessToken1, spec);

        //then
        알림_전체_조회_응답을_검증한다(response, 1);
    }

    @Test
    @DisplayName("다른 사람이 프로필 좋아요 시 알림 조회 가능")
    void profile_like_alarm_success() throws IOException {
        //given
        String accessToken1=토킷_완전_온보딩(); //2
        String accessToken2=원준_완전_온보딩(); //1

        좋아요를_누른다(spec, accessToken2, 2L);

        //when

        ExtractableResponse<Response> response =  알림을_조회한다(accessToken1, spec);

        //then
        알림_전체_조회_응답을_검증한다(response, 1);
    }

    @Test
    @DisplayName("다른 사람이 채팅방을 만들 시 알림 조회 가능")
    void chatroom_alarm_success() throws IOException {
        //given
        String accessToken1=토킷_완전_온보딩(); //2
        String accessToken2=원준_완전_온보딩(); //1

        _1대1_채팅방을_생성_또는_조회한다(accessToken2, 2L, spec);

        //when

        ExtractableResponse<Response> response =  알림을_조회한다(accessToken1, spec);

        //then
        알림_전체_조회_응답을_검증한다(response, 1);
    }

    @Test
    @DisplayName("자기 자신 피드에 댓글 작성 시 조회 불가")
    void comment_alarm_fail() throws IOException {
        //given

        String accessToken1=토킷_완전_온보딩();

        ExtractableResponse<Response> resp1 = 피드를_작성한다(accessToken1, spec, "테스트", "테스트");
        Long feedId = resp1.jsonPath().getLong("feedId");
        Map<String, Object> saveReq = new HashMap<>();
        saveReq.put("content", "테스트");
        피드의_댓글을_작성한다(saveReq, feedId, accessToken1, spec);

        //when

        ExtractableResponse<Response> response =  알림을_조회한다(accessToken1, spec);

        //then
        알림_전체_조회_응답을_검증한다(response, 0);
    }

    @Test
    @DisplayName("자기 자신 피드에 좋아요 시 조회 불가")
    void feed_like_alarm_fail() throws IOException {
        //given

        String accessToken1=토킷_완전_온보딩();

        ExtractableResponse<Response> resp1 = 피드를_작성한다(accessToken1, spec, "테스트", "테스트");
        Long feedId = resp1.jsonPath().getLong("feedId");
        피드에_좋아요를_누른다(accessToken1, spec, feedId);

        //when

        ExtractableResponse<Response> response =  알림을_조회한다(accessToken1, spec);

        //then
        알림_전체_조회_응답을_검증한다(response, 0);
    }

    @Test
    @DisplayName("종합 case에 대해 알림 조회 시 의도대로 성공한다")
    void all_alarm_success() throws IOException {
        api_문서_타이틀("fetch_all_notifications", spec);

        //given
        String accessToken1=토킷_완전_온보딩(); //2
        String accessToken2=원준_완전_온보딩(); //1

        ExtractableResponse<Response> resp1 = 피드를_작성한다(accessToken1, spec, "테스트", "테스트");
        Long feedId = resp1.jsonPath().getLong("feedId");
        Map<String, Object> saveReq = new HashMap<>();
        saveReq.put("content", "테스트");
        피드의_댓글을_작성한다(saveReq, feedId, accessToken2, spec);
        피드에_좋아요를_누른다(accessToken2, spec, feedId);
        _1대1_채팅방을_생성_또는_조회한다(accessToken2, 2L, spec);
        좋아요를_누른다(spec, accessToken2, 2L);

        //when

        ExtractableResponse<Response> response =  알림을_조회한다(accessToken1, spec);

        //then
        알림_전체_조회_응답을_검증한다(response, 4);
    }
}

