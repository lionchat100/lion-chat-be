package com.lion.be.feed_comment.service;

import java.util.Objects;

import com.lion.be.global.util.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedCommentLikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void likeComment(Long commentId, Long userId) {
        String likedUsersKey = RedisKey.COMMENT_LIKED_USERS_KEY_PREFIX + commentId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().add(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().increment(RedisKey.COMMENT_LIKE_COUNT_KEY_PREFIX + commentId);
            redisTemplate.opsForSet().add(RedisKey.DIRTY_COMMENT_LIKE_KEY, String.valueOf(commentId));
        }
    }

    public void unlikeComment(Long commentId, Long userId) {
        String likedUsersKey = RedisKey.COMMENT_LIKED_USERS_KEY_PREFIX + commentId;
        String userIdStr = String.valueOf(userId);

        if (Objects.equals(redisTemplate.opsForSet().remove(likedUsersKey, userIdStr), 1L)) {
            redisTemplate.opsForValue().decrement(RedisKey.COMMENT_LIKE_COUNT_KEY_PREFIX + commentId);
            redisTemplate.opsForSet().add(RedisKey.DIRTY_COMMENT_LIKE_KEY, String.valueOf(commentId));
        }
    }

}