package com.lion.be.feed.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedLikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    // ✨ 피드용 Redis 키 접두사 정의
    private static final String LIKE_COUNT_KEY_PREFIX = "feed:like_count:";
    private static final String LIKED_USERS_KEY_PREFIX = "feed:liked_users:";
    private static final String DIRTY_FEEDS_KEY = "dirty:feeds";

    public void likeFeed(Long feedId, Long userId) {
        String likedUsersKey = LIKED_USERS_KEY_PREFIX + feedId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().add(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().increment(LIKE_COUNT_KEY_PREFIX + feedId);
            redisTemplate.opsForSet().add(DIRTY_FEEDS_KEY, String.valueOf(feedId));
        }
    }

    public void unlikeFeed(Long feedId, Long userId) {
        String likedUsersKey = LIKED_USERS_KEY_PREFIX + feedId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().remove(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().decrement(LIKE_COUNT_KEY_PREFIX + feedId);
            redisTemplate.opsForSet().add(DIRTY_FEEDS_KEY, String.valueOf(feedId));
        }
    }

}
