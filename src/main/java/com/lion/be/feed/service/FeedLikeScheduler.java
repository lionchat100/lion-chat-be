package com.lion.be.feed.service;

import com.lion.be.feed.repository.FeedRepository;
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
public class FeedLikeScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedRepository feedRepository;

    private static final String LIKE_COUNT_KEY_PREFIX = "feed:like_count:";
    private static final String DIRTY_FEEDS_KEY = "dirty:feeds";

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void syncLikesToDb() {
        log.info("피드 좋아요 카운트 Batch update 시작");

        List<Object> objFeedIds = redisTemplate.opsForSet().pop(DIRTY_FEEDS_KEY, 100);

        if (objFeedIds == null || objFeedIds.isEmpty()) {
            log.info("업데이트 할 피드 좋아요가 존재하지 않음");
            return;
        }

        for (Object feedIdObj : objFeedIds) {
            String feedIdStr = (String) feedIdObj;
            Long feedId = Long.parseLong(feedIdStr);
            String likeCountKey = LIKE_COUNT_KEY_PREFIX + feedId;
            Object likeCountObj = redisTemplate.opsForValue().get(likeCountKey);

            if (likeCountObj != null) {
                long likeCount;
                if (likeCountObj instanceof Number) {
                    likeCount = ((Number) likeCountObj).longValue();
                } else {
                    likeCount = Long.parseLong(likeCountObj.toString());
                }
                feedRepository.updateLikeCount(feedId, likeCount);
                log.debug("업데이트 feedId: {}, likeCount: {}", feedId, likeCount);
            }
        }

        log.info("피드 댓글 좋아요 카운트 Batch update 끝: {}번", objFeedIds.size());
    }

}
