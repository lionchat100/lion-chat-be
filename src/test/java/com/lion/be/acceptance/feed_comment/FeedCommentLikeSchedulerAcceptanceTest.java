package com.lion.be.acceptance.feed_comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.lion.be.acceptance.AcceptanceTest;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.feed_comment.domain.entity.FeedComment;
import com.lion.be.feed_comment.repository.persistence.jpa.FeedCommentJpaRepository;
import com.lion.be.feed_comment.service.FeedCommentLikeScheduler;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.repository.persistence.jpa.UserJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("피드 댓글 좋아요 스케줄러 인수 테스트")
class FeedCommentLikeSchedulerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private FeedCommentLikeScheduler feedCommentLikeScheduler;

    @Autowired
    private FeedCommentJpaRepository feedCommentJpaRepository;

    @Autowired
    private FeedRepository feedJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager em;

    private static final String LIKE_COUNT_KEY_PREFIX = "comment:like_count:";
    private static final String DIRTY_COMMENTS_KEY = "dirty:comments";

    @DisplayName("스케줄러가 실행되면 Redis의 좋아요 수가 DB에 정확히 반영된다")
    @Transactional
    @Test
    void when_scheduler_runs_then_likes_are_synced_to_db() {
        // given
        User user = userJpaRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Feed feed = new Feed("스케줄러 테스트 피드", "내용입니다.", user);
        feedJpaRepository.save(feed);

        FeedComment comment = FeedComment.of(feed, user, "스케줄러 테스트 댓글");
        feedCommentJpaRepository.save(comment);

        Long commentId = comment.getId();

        assertThat(comment.getLikeCount()).isZero();

        String likeCountKey = LIKE_COUNT_KEY_PREFIX + commentId;
        redisTemplate.opsForValue().set(likeCountKey, "12");
        redisTemplate.opsForSet().add(DIRTY_COMMENTS_KEY, String.valueOf(commentId));

        // when
        feedCommentLikeScheduler.syncLikesToDb();

        // then
        em.flush();
        em.clear();

        FeedComment updatedComment = feedCommentJpaRepository.findById(commentId).orElseThrow();
        assertThat(updatedComment.getLikeCount()).isEqualTo(12);

        Long dirtySetSize = redisTemplate.opsForSet().size(DIRTY_COMMENTS_KEY);
        assertThat(dirtySetSize).isZero();
    }

}
