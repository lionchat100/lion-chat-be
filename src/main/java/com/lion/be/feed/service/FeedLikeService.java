package com.lion.be.feed.service;

import java.util.Objects;

import com.lion.be.global.util.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedLikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void likeFeed(Long feedId, Long userId) {
        String likedUsersKey = RedisKey.FEED_LIKED_USERS_KEY_PREFIX + feedId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().add(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().increment(RedisKey.FEED_LIKE_COUNT_KEY_PREFIX + feedId);
            redisTemplate.opsForSet().add(RedisKey.DIRTY_FEED_LIKE_KEY, String.valueOf(feedId));
            redisTemplate.opsForSet().add(RedisKey.USER_LIKED_FEED_SET_PREFIX + userIdStr, String.valueOf(feedId));
        }
    }

    public void unlikeFeed(Long feedId, Long userId) {
        String likedUsersKey = RedisKey.FEED_LIKED_USERS_KEY_PREFIX + feedId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().remove(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().decrement(RedisKey.FEED_LIKE_COUNT_KEY_PREFIX + feedId);
            redisTemplate.opsForSet().add(RedisKey.DIRTY_FEED_LIKE_KEY, String.valueOf(feedId));
            redisTemplate.opsForSet().remove(RedisKey.USER_LIKED_FEED_SET_PREFIX + userIdStr, String.valueOf(feedId));
        }
    }

}
