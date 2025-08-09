package com.lion.be.feed_comment.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCommentLikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_COUNT_KEY_PREFIX = "comment:like_count:";
    private static final String LIKED_USERS_KEY_PREFIX = "comment:liked_users:";
    private static final String DIRTY_COMMENTS_KEY = "dirty:comments";

    public void likeComment(Long commentId, Long userId) {
        String likedUsersKey = LIKED_USERS_KEY_PREFIX + commentId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().add(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().increment(LIKE_COUNT_KEY_PREFIX + commentId);
            redisTemplate.opsForSet().add(DIRTY_COMMENTS_KEY, String.valueOf(commentId));
        }
    }

    public void unlikeComment(Long commentId, Long userId) {
        String likedUsersKey = LIKED_USERS_KEY_PREFIX + commentId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().remove(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().decrement(LIKE_COUNT_KEY_PREFIX + commentId);
            redisTemplate.opsForSet().add(DIRTY_COMMENTS_KEY, String.valueOf(commentId));
        }
    }

}