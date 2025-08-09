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
        log.info("Batch update for feed likes started.");
        List<Object> dirtyFeedIds = redisTemplate.opsForSet().pop(DIRTY_FEEDS_KEY, 100);

        if (dirtyFeedIds == null || dirtyFeedIds.isEmpty()) {
            log.info("No dirty feeds to update.");
            return;
        }

        for (Object feedIdObj : dirtyFeedIds) {
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
                log.debug("Updating feedId: {} with likeCount: {}", feedId, likeCount);
            }
        }
        log.info("Batch update for {} feed likes finished.", dirtyFeedIds.size());
    }

}
