package com.lion.be.feed_comment.service;

import com.lion.be.feed_comment.repository.FeedCommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedCommentLikeScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedCommentRepository feedCommentRepository;

    private static final String LIKE_COUNT_KEY_PREFIX = "comment:like_count:";
    private static final String DIRTY_COMMENTS_KEY = "dirty:comments";

    // 10초마다 실행
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void syncLikesToDb() {
        log.info("좋아요 카운트 Batch update 시작");

        List<Object> dirtyCommentIds = redisTemplate.opsForSet().pop(DIRTY_COMMENTS_KEY, 100);

        if (dirtyCommentIds == null || dirtyCommentIds.isEmpty()) {
            log.info("No dirty comments to update.");
            return;
        }

        for (Object commentIdObj : dirtyCommentIds) {
            String commentIdStr = (String) commentIdObj;
            Long commentId = Long.parseLong(commentIdStr);

            String likeCountKey = LIKE_COUNT_KEY_PREFIX + commentId;
            Object likeCountObj = redisTemplate.opsForValue().get(likeCountKey);

            if (likeCountObj != null) {
                long likeCount;
                if (likeCountObj instanceof Number) {
                    // increment/decrement로 저장된 경우 (Number 타입)
                    likeCount = ((Number) likeCountObj).longValue();
                } else {
                    // set으로 저장된 경우 (String 타입)
                    likeCount = Long.parseLong(likeCountObj.toString());
                }

                feedCommentRepository.updateLikeCount(commentId, likeCount);
                log.debug("Updating commentId: {} with likeCount: {}", commentId, likeCount);
            }
        }

        log.info("좋아요 카운트 Batch update 끝: {}번", dirtyCommentIds.size());
    }

}
