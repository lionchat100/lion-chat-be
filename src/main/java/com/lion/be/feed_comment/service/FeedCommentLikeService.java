package com.lion.be.feed_comment.service;

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

        // SADD를 사용하여 Set에 사용자를 추가. 성공하면 1, 이미 있으면 0 반환 (원자적)
        if (redisTemplate.opsForSet().add(likedUsersKey, userIdStr) == 1) {
            // INCR을 사용하여 좋아요 수를 1 증가 (원자적)
            redisTemplate.opsForValue().increment(LIKE_COUNT_KEY_PREFIX + commentId);
            // DB에 업데이트가 필요한 댓글 ID를 Set에 추가
            redisTemplate.opsForSet().add(DIRTY_COMMENTS_KEY, String.valueOf(commentId));
        }
    }

    public void unlikeComment(Long commentId, Long userId) {
        String likedUsersKey = LIKED_USERS_KEY_PREFIX + commentId;
        String userIdStr = String.valueOf(userId);

        // SREM을 사용하여 Set에서 사용자를 제거. 성공하면 1, 없으면 0 반환 (원자적)
        if (redisTemplate.opsForSet().remove(likedUsersKey, userIdStr) == 1) {
            // DECR을 사용하여 좋아요 수를 1 감소 (원자적)
            redisTemplate.opsForValue().decrement(LIKE_COUNT_KEY_PREFIX + commentId);
            // DB에 업데이트가 필요한 댓글 ID를 Set에 추가
            redisTemplate.opsForSet().add(DIRTY_COMMENTS_KEY, String.valueOf(commentId));
        }
    }

}