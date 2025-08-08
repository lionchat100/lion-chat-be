package com.lion.be.feed_comment.repository;

import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.domain.dto.FeedCommentSaveResponse;
import com.lion.be.feed_comment.domain.entity.FeedComment;
import com.lion.be.feed_comment.repository.persistence.jpa.FeedCommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FeedCommentRepositoryImpl implements FeedCommentRepository {

    private final FeedCommentJpaRepository feedCommentJpaRepository;

    @Override
    public Slice<FeedCommentResponse> fetchAllByFeedId(Long feedId, Pageable pageable) {
        return feedCommentJpaRepository.fetchAllByFeedId(feedId, pageable);
    }

    @Override
    public FeedCommentSaveResponse save(FeedComment feedComment) {
        FeedComment savedFeedComment = feedCommentJpaRepository.save(feedComment);
        return new FeedCommentSaveResponse(savedFeedComment.getId());
    }

    @Override
    public void deleteById(Long id) {
        feedCommentJpaRepository.softDeleteById(id);
    }

}
