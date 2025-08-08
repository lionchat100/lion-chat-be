package com.lion.be.feed_comment.service;

import com.lion.be.feed.domain.entity.Feed;
import com.lion.be.feed.service.FeedReadService;
import com.lion.be.feed_comment.domain.dto.FeedCommentSaveRequest;
import com.lion.be.feed_comment.domain.dto.FeedCommentSaveResponse;
import com.lion.be.feed_comment.domain.entity.FeedComment;
import com.lion.be.feed_comment.repository.FeedCommentRepository;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedCommentWriteService {

    private final FeedCommentRepository feedCommentRepository;
    private final FeedReadService feedReadService;
    private final UserReadService userReadService;

    public FeedCommentSaveResponse save(Long feedId, Long userId, FeedCommentSaveRequest request) {
        Feed feed = feedReadService.fetchById(feedId);
        User user = userReadService.fetchById(userId);

        FeedComment newComment = FeedComment.of(feed, user, request.getContent());
        FeedCommentSaveResponse savedResponse = feedCommentRepository.save(newComment);

        return new FeedCommentSaveResponse(savedResponse.commentId());
    }

    public void delete(Long id) {
        feedCommentRepository.deleteById(id);
    }

}
