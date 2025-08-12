package com.lion.be.feed_comment.repository.persistence.jpa;

import com.lion.be.feed_comment.domain.dto.FeedCommentResponse;
import com.lion.be.feed_comment.domain.entity.FeedComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedCommentJpaRepository extends JpaRepository<FeedComment, Long> {

    @Query("SELECT NEW com.lion.be.feed_comment.domain.dto.FeedCommentResponse(" +
            "c.id, c.feed.id, c.content, c.createdAt, c.updatedAt, u.id, u.name, u.imageUrl, c.likeCount" // c.likeCount 추가
            + ") " +
            "FROM FeedComment c " +
            "JOIN c.user u " +
            "WHERE c.feed.id = :feedId AND c.isDeleted = false")
    Slice<FeedCommentResponse> fetchAllByFeedId(@Param("feedId") Long feedId, Pageable pageable);

    @Modifying
    @Query("UPDATE FeedComment c "
            + "SET c.isDeleted = true "
            + "WHERE c.id = :id")
    void softDeleteById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FeedComment c "
            + "SET c.likeCount = :likeCount "
            + "WHERE c.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount") long likeCount);

    @Query("SELECT NEW com.lion.be.feed_comment.domain.dto.FeedCommentResponse(" +
            "c.id, c.feed.id, c.content, c.createdAt, c.updatedAt, u.id, u.name, u.imageUrl, c.likeCount" // c.likeCount 추가
            + ") " +
            "FROM FeedComment c " +
            "JOIN c.user u " +
            "WHERE c.id = :commentId AND c.isDeleted = false")
    FeedCommentResponse findCommentById(@Param("commentId") Long commentId);

    @Modifying(clearAutomatically = true)
    @Query("""
        update FeedComment c
        set c.isDeleted = true
        where c.feed.id = :feedId
    """)
    void softDeleteByFeedId(@Param("feedId") Long feedId);

}
