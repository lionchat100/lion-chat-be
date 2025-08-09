package com.lion.be.acceptance.feed;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.feed.service.FeedCommentCountScheduler;
import com.lion.be.feed.service.FeedLikeScheduler;
import com.lion.be.global.util.RedisKey;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("피드 댓글 수 스케줄러 인수 테스트")
public class FeedCommentCountSchedulerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private FeedCommentCountScheduler feedCommentCountScheduler;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    @DisplayName("스케줄러가 실행되면 Redis의 피드 댓글 수가 DB에 정확히 반영된다")
    void when_scheduler_runs_then_feed_likes_are_synced_to_db() {
        // given: 테스트 데이터 설정
        User user = userRepository.fetchById(1L)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Feed feed = new Feed("스케줄러 테스트 피드", "내용입니다.", user);
        feedRepository.save(feed);
        Long feedId = feed.getId();

        assertThat(feed.getCommentCount()).isZero(); // 초기 DB의 좋아요 수는 0

        // Redis에 '댓글'이 25번 업로드된 상황을 시뮬레이션
        String commentCountKey = RedisKey.COMMENT_COUNT_KEY + feedId;
        redisTemplate.opsForValue().set(commentCountKey, "25");
        redisTemplate.opsForSet().add(RedisKey.DIRTY_COMMENT_COUNT_KEY, String.valueOf(feedId));

        // when: 스케줄러의 메서드를 직접 호출
        feedCommentCountScheduler.syncLikesToDb();

        // then: DB와 Redis의 상태 변화를 검증
        em.flush();
        em.clear();

        Feed updatedFeed = feedRepository.findById(feedId).orElseThrow();
        assertThat(updatedFeed.getCommentCount()).isEqualTo(25);

        Long dirtySetSize = redisTemplate.opsForSet().size(RedisKey.DIRTY_COMMENT_COUNT_KEY);
        assertThat(dirtySetSize).isZero();
    }

}