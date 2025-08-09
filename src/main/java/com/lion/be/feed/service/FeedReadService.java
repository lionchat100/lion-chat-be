package com.lion.be.feed.service;

import com.lion.be.feed.domain.dto.FeedDto;
import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedReadService {

    private final FeedRepository feedRepository;
    private final RedisTemplate<String, Object> redisTemplate; // ✨ RedisTemplate 주입

    private static final int DEFAULT_PAGE_SIZE = 30;
    private static final String LIKE_COUNT_KEY_PREFIX = "feed:like_count:";
    private static final String LIKED_USERS_KEY_PREFIX = "feed:liked_users:";

    public Feed fetchById(Long id) {
        return feedRepository.findFeed(id)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
    }

    public FeedResponse getFeedResponseById(Long feedId, Long currentUserId) {
        Feed feed = feedRepository.fetchById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

        long likeCount = getLikeCountFromRedis(feed);
        boolean isLiked = isLikedByCurrentUser(feedId, currentUserId);
        long commentCount = 0L; // TODO: 댓글 수 조회 로직 필요

        return new FeedResponse(
                feed.getId(), feed.getTitle(), feed.getContent(), feed.getCreatedAt(),
                likeCount, isLiked, commentCount,
                feed.getUser().getName(), feed.getUser().getId(), feed.getUser().getImageUrl()
        );
    }

    // ✨ currentUserId를 받아 Redis 데이터와 조합하도록 로직 변경
    public Slice<FeedResponse> getRecentFeedsFirst(Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchRecentFeedsFirst(getRecentPageable(size));
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    public Slice<FeedResponse> getRecentFeedsAfter(Long lastId, Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchRecentFeedsAfter(lastId, getRecentPageable(size));
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    public Slice<FeedResponse> getHotFeedsFirst(Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchHotFeedsFirst(getHotPageable());
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    public Slice<FeedResponse> getHotFeedsAfter(Long lastLikeCount, Long lastId, Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchHotFeedsAfter(lastLikeCount, lastId, getHotPageable());
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    private Slice<FeedResponse> enrichFeedsWithRedisData(Slice<FeedResponse> feeds, Long currentUserId) {
        feeds.getContent().forEach(feedResponse -> {
            FeedDto feedDto = feedResponse.getFeed();
            long feedId = feedDto.getId();

            Object likeCountObj = redisTemplate.opsForValue().get(LIKE_COUNT_KEY_PREFIX + feedId);
            if (likeCountObj != null) {
                feedDto.setLikeCount(((Number) likeCountObj).longValue());
            }

            if (currentUserId != null) {
                boolean isLiked = Boolean.TRUE.equals(redisTemplate.opsForSet()
                        .isMember(LIKED_USERS_KEY_PREFIX + feedId, String.valueOf(currentUserId)));
                feedDto.setIsLiked(isLiked);
            }
        });
        return feeds;
    }

    private long getLikeCountFromRedis(Feed feed) {
        Object likeCountObj = redisTemplate.opsForValue().get(LIKE_COUNT_KEY_PREFIX + feed.getId());
        return (likeCountObj != null) ? ((Number) likeCountObj).longValue() : feed.getLikeCount();
    }

    private boolean isLikedByCurrentUser(Long feedId, Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(LIKED_USERS_KEY_PREFIX + feedId, String.valueOf(currentUserId)));
    }

    private Pageable getRecentPageable(Integer size) {
        return PageRequest.of(0, size != null && size <= DEFAULT_PAGE_SIZE ? size : DEFAULT_PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "id"));
    }

    private Pageable getHotPageable() {
        return PageRequest.of(0, DEFAULT_PAGE_SIZE);
    }

}