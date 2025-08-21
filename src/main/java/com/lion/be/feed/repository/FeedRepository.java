package com.lion.be.feed.repository;

import com.lion.be.feed.domain.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    @Query("select f from Feed f join fetch f.user where f.id = :id and f.isDeleted = false")
    Optional<Feed> fetchById(Long id);

    @Query("select f from Feed f join fetch f.user u where f.id = :id and f.isDeleted = false and u.role <> 'BANNED'")
    Optional<Feed> findFeed(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Feed f SET f.likeCount = :likeCount WHERE f.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount") long likeCount);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Feed f SET f.commentCount = :commentCount WHERE f.id = :id")
    void updateCommentCount(@Param("id") Long id, @Param("commentCount") long commentCount);

    @Modifying(clearAutomatically = true)
    @Query("""
        update Feed f
        set f.isDeleted = true
        where f.id = :feedId
    """)
    void softDeleteById(@Param("feedId") Long feedId);

}
