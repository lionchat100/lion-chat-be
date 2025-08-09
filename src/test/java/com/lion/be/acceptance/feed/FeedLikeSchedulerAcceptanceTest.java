package com.lion.be.acceptance.feed;

import static org.assertj.core.api.Assertions.assertThat;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.feed.service.FeedLikeScheduler;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("피드 좋아요 스케줄러 인수 테스트")
public class FeedLikeSchedulerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private FeedLikeScheduler feedLikeScheduler;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private static final String LIKE_COUNT_KEY_PREFIX = "feed:like_count:";
    private static final String DIRTY_FEEDS_KEY = "dirty:feeds";

    @Test
    @Transactional
    @DisplayName("스케줄러가 실행되면 Redis의 피드 좋아요 수가 DB에 정확히 반영된다")
    void when_scheduler_runs_then_feed_likes_are_synced_to_db() {
        // given: 테스트 데이터 설정
        User user = userRepository.fetchById(1L)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Feed feed = new Feed("스케줄러 테스트 피드", "내용입니다.", user);
        feedRepository.save(feed);
        Long feedId = feed.getId();

        assertThat(feed.getLikeCount()).isZero(); // 초기 DB의 좋아요 수는 0

        // Redis에 '좋아요'가 25번 눌린 상황을 시뮬레이션
        String likeCountKey = LIKE_COUNT_KEY_PREFIX + feedId;
        redisTemplate.opsForValue().set(likeCountKey, "25");
        redisTemplate.opsForSet().add(DIRTY_FEEDS_KEY, String.valueOf(feedId));

        // when: 스케줄러의 메서드를 직접 호출
        feedLikeScheduler.syncLikesToDb();

        // then: DB와 Redis의 상태 변화를 검증
        em.flush();
        em.clear();

        Feed updatedFeed = feedRepository.findById(feedId).orElseThrow();
        assertThat(updatedFeed.getLikeCount()).isEqualTo(25);

        Long dirtySetSize = redisTemplate.opsForSet().size(DIRTY_FEEDS_KEY);
        assertThat(dirtySetSize).isZero();
    }

}