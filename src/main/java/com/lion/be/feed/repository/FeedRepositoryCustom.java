package com.lion.be.feed.repository;

import com.lion.be.feed.domain.dto.FeedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FeedRepositoryCustom {

    void batchUpdateFeedLikeCount(List<Long> feedIds, List<Long> likeCounts);
    void batchUpdateFeedCommentCount(List<Long> feedIds, List<Long> commentCounts);

//    Slice<FeedResponse> fetchRecentFeedsFirst(Pageable pageable);
//    Slice<FeedResponse> fetchRecentFeedsAfter(Long lastId, Pageable pageable);
//    Slice<FeedResponse> fetchHotFeedsFirst(Pageable pageable);
//    Slice<FeedResponse> fetchHotFeedsAfter(Long lastLikeCount, Long lastId, Pageable pageable);
//    Slice<FeedResponse> fetchFeedsByUserIdFirst(Pageable pageable);
//    Slice<FeedResponse> fetchFeedsByUserIdAfter(Long lastId, Pageable pageable);

}
