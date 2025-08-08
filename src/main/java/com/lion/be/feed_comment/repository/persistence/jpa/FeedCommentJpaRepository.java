package com.lion.be.feed_comment.repository.persistence.jpa;

import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.domain.entity.FeedComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedCommentJpaRepository extends JpaRepository<FeedComment, Long> {

    @Query("SELECT NEW com.lion.be.feed_comment.domain.dto.FeedCommentResponse(" +
            "c.id, c.feed.id, c.content, c.createdAt, c.updatedAt, u.id, u.name, u.imageUrl"
            + ") " +
            "FROM FeedComment c " +
            "JOIN c.user u " +
            "WHERE c.feed.id = :feedId AND c.isDeleted = false")
    Slice<FeedCommentResponse> fetchCommentsByFeedId(@Param("feedId") Long feedId, Pageable pageable);

}
