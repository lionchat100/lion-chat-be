package com.lion.be.feed.repository;

import com.lion.be.feed.domain.dto.FeedResponse;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FeedRepositoryCustom {

    void batchUpdateFeedLikeCount(List<Long> feedIds, List<Long> likeCounts);
    void batchUpdateFeedCommentCount(List<Long> feedIds, List<Long> commentCounts);
}
