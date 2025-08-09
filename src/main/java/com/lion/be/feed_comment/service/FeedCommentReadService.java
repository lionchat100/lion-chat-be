package com.lion.be.feed_comment.service;

import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.repository.FeedCommentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedCommentReadService {

    private final FeedCommentRepository feedCommentRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_COUNT_KEY_PREFIX = "comment:like_count:";
    private static final String LIKED_USERS_KEY_PREFIX = "comment:liked_users:";

    public Slice<FeedCommentResponse> fetchAll(Long feedId, Pageable pageable, Long userId) {
        Slice<FeedCommentResponse> slice = feedCommentRepository.fetchAllByFeedId(feedId, pageable);

        List<FeedCommentResponse> enrichedContent = slice.getContent().stream()
                .map(comment -> {
                    String likeCountKey = LIKE_COUNT_KEY_PREFIX + comment.id();
                    String likedUsersKey = LIKED_USERS_KEY_PREFIX + comment.id();

                    Object likeCountObj = redisTemplate.opsForValue().get(likeCountKey);
                    long likeCount = (likeCountObj != null) ?
                            ((Number) likeCountObj).longValue()
                            : comment.likeCount();

                    boolean isLiked = Boolean.TRUE.equals(
                            redisTemplate.opsForSet().isMember(likedUsersKey, String.valueOf(userId))
                    );

                    return new FeedCommentResponse(
                            comment.id(),
                            comment.feedId(),
                            comment.feedCommentUserResponse(),
                            comment.content(),
                            likeCount,
                            isLiked,
                            comment.createdAt(),
                            comment.updatedAt()
                    );
                }).collect(Collectors.toList());

        return new SliceImpl<>(enrichedContent, slice.getPageable(), slice.hasNext());
    }

}
