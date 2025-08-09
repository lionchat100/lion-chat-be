package com.lion.be.feed_comment.service;

import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.repository.FeedCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedCommentReadService {

    private final FeedCommentRepository feedCommentRepository;

    public Slice<FeedCommentResponse> fetchAll(Long feedId, Pageable pageable) {
        return feedCommentRepository.fetchAllByFeedId(feedId, pageable);
    }

}
