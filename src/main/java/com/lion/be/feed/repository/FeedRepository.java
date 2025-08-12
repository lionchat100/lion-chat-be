package com.lion.be.feed.repository;

import com.lion.be.feed.domain.dto.FeedResponse;
import com.lion.be.feed.domain.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    //첫 전체 조회
    @Query("""
            SELECT new com.lion.be.feed.domain.dto.FeedResponse(
                f.id, f.title, f.content, f.createdAt,
                f.likeCount, f.commentCount,
                u.name, u.id, u.imageUrl
            )
            FROM Feed f JOIN f.user u
            WHERE f.isDeleted = false
            """)
    Slice<FeedResponse> fetchRecentFeedsFirst(Pageable pageable);

    @Query("select f from Feed f join fetch f.user where f.id = :id and f.isDeleted = false")
    Optional<Feed> fetchById(Long id);

    //이후 전체 조회
    @Query("""
            SELECT new com.lion.be.feed.domain.dto.FeedResponse(
                f.id, f.title, f.content, f.createdAt,
                f.likeCount, f.commentCount,
                u.name, u.id, u.imageUrl
            )
            FROM Feed f JOIN f.user u
            WHERE f.isDeleted = false AND f.id < :lastId
            """)
    Slice<FeedResponse> fetchRecentFeedsAfter(Long lastId, Pageable pageable);

    //첫 좋아요 순 조회(커서 페이지네이션(1차적으로 like_count, 2차적으로는 게시글의 pk), 페이징, 기간 별 or 전체 기간)
    @Query("""
            SELECT new com.lion.be.feed.domain.dto.FeedResponse(
                f.id, f.title, f.content, f.createdAt,
                f.likeCount, f.commentCount,
                u.name, u.id, u.imageUrl
            )
            FROM Feed f JOIN f.user u
            WHERE f.isDeleted = false
            ORDER BY f.likeCount DESC, f.id DESC
            """)
    Slice<FeedResponse> fetchHotFeedsFirst(Pageable pageable);

    //이후 좋아요 순 조회
    @Query("""
            SELECT new com.lion.be.feed.domain.dto.FeedResponse(
                f.id, f.title, f.content, f.createdAt,
                f.likeCount, f.commentCount,
                u.name, u.id, u.imageUrl
            )
            FROM Feed f JOIN f.user u
            WHERE f.isDeleted = false
            AND (f.likeCount < :lastLikeCount OR (f.likeCount = :lastLikeCount AND f.id < :lastId))
            ORDER BY f.likeCount DESC, f.id DESC
            """)
    Slice<FeedResponse> fetchHotFeedsAfter(Long lastLikeCount, Long lastId, Pageable pageable);

    @Query("select f from Feed f where f.id = :id and f.isDeleted = false")
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

    //생각할 것: 검색이 되는가? 검색이 된다면 어디까지 될 것인가?
}
