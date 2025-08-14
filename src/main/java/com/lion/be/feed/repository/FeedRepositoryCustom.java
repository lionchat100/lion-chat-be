package com.lion.be.feed.repository;

import java.util.List;

public interface FeedRepositoryCustom {

    void batchUpdateFeedLikeCount(List<Long> feedIds, List<Long> likeCounts);
    void batchUpdateFeedCommentCount(List<Long> feedIds, List<Long> commentCounts);
}
