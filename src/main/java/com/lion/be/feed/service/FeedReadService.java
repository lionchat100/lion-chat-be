package com.lion.be.feed.service;

import com.lion.be.feed.domain.dto.FeedDto;
import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.repository.FeedRepository;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.global.util.RedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedReadService {

    private final FeedRepository feedRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int DEFAULT_PAGE_SIZE = 30;

    public Feed fetchById(Long id) {
        return feedRepository.findFeed(id)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
    }

    public FeedResponse getFeedResponseById(Long feedId, Long currentUserId) {
        Feed feed = feedRepository.fetchById(feedId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

        long likeCount = getAndCacheLikeCount(feed.getId(), feed.getLikeCount());
        boolean isLiked = isLikedByCurrentUser(feedId, currentUserId);
        long commentCount = getAndCacheCommentCount(feed.getId(), feed.getCommentCount());

        return new FeedResponse(
                feed.getId(), feed.getTitle(), feed.getContent(), feed.getCreatedAt(),
                likeCount, isLiked, commentCount,
                feed.getUser().getName(), feed.getUser().getId(), feed.getUser().getImageUrl()
        );
    }

    public Slice<FeedResponse> getRecentFeedsFirst(Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchRecentFeedsFirst(getRecentPageable(size));
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    public Slice<FeedResponse> getRecentFeedsAfter(Long lastId, Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchRecentFeedsAfter(lastId, getRecentPageable(size));
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    public Slice<FeedResponse> getHotFeedsFirst(Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchHotFeedsFirst(getHotPageable(size));
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    public Slice<FeedResponse> getHotFeedsAfter(Long lastLikeCount, Long lastId, Integer size, Long currentUserId) {
        Slice<FeedResponse> feedResponses = feedRepository.fetchHotFeedsAfter(lastLikeCount, lastId, getHotPageable(size));
        return enrichFeedsWithRedisData(feedResponses, currentUserId);
    }

    private Slice<FeedResponse> enrichFeedsWithRedisData(Slice<FeedResponse> feeds, Long currentUserId) {
        feeds.getContent().forEach(feedResponse -> {
            FeedDto feedDto = feedResponse.getFeed();
            long feedId = feedDto.getId();

            // ✨ 수정: 캐시 워밍업 로직을 포함한 메서드 호출
            long finalLikeCount = getAndCacheLikeCount(feedId, feedDto.getLikeCount());
            feedDto.setLikeCount(finalLikeCount);

            long finalCommentCount = getAndCacheCommentCount(feedId, feedDto.getCommentCount());
            feedDto.setCommentCount(finalCommentCount);

            if (currentUserId != null) {
                if(isLikedByCurrentUser(feedId, currentUserId)){
                    feedDto.like();
                } else {
                    feedDto.unlike();
                }
            }
        });
        return feeds;
    }

    private long getAndCacheLikeCount(Long feedId, long dbLikeCount) {
        String likeCountKey = RedisKey.FEED_LIKE_COUNT_KEY_PREFIX + feedId;
        Object likeCountObj = redisTemplate.opsForValue().get(likeCountKey);

        if (likeCountObj != null) {
            return ((Number) likeCountObj).longValue();
        } else {
            redisTemplate.opsForValue().set(likeCountKey, dbLikeCount);
            return dbLikeCount;
        }
    }

    private long getAndCacheCommentCount(Long feedId, long dbCommentCount) {
        String commentCountKey = RedisKey.COMMENT_COUNT_KEY + feedId;
        Object commentCountObj = redisTemplate.opsForValue().get(commentCountKey);

        if (commentCountObj != null) {
            return ((Number) commentCountObj).longValue();
        } else {
            redisTemplate.opsForValue().set(commentCountKey, dbCommentCount);
            return dbCommentCount;
        }
    }

    private boolean isLikedByCurrentUser(Long feedId, Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(RedisKey.FEED_LIKED_USERS_KEY_PREFIX + feedId, String.valueOf(currentUserId)));
    }

    private Pageable getRecentPageable(Integer size) {
        return PageRequest.of(0,
                size != null && size <= DEFAULT_PAGE_SIZE ? size : DEFAULT_PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "id"));
    }

    private Pageable getHotPageable(Integer size) {
        return PageRequest.of(0,
                size != null && size <= DEFAULT_PAGE_SIZE ? size : DEFAULT_PAGE_SIZE);
    }

}