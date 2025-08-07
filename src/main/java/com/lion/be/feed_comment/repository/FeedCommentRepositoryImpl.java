package com.lion.be.feed_comment.repository;

import com.lion.be.feed_comment.repository.persistence.jpa.FeedCommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FeedCommentRepositoryImpl implements FeedCommentRepository {

    private final FeedCommentJpaRepository feedCommentJpaRepository;

}
