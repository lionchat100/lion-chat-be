package com.lion.be.feed_comment.repository;

import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.domain.dto.FeedCommentSaveResponse;
import com.lion.be.feed_comment.domain.entity.FeedComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FeedCommentRepository {

    Slice<FeedCommentResponse> fetchAllByFeedId(Long feedId, Pageable pageable);

    FeedCommentSaveResponse save(FeedComment feedComment);

    void deleteById(Long id);

    void updateLikeCount(Long commentId, long likeCount);

    FeedCommentResponse findCommentById(Long commentId);

    void softDeleteByFeedId(Long feedId);

}
